package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class ForestCellularAutomaton1 implements CellularAutomaton {

  protected static final int[][] ODD_NEAR_NEIGHS =
      {{-1, 0}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}};
  protected static final int[][] ODD_DISTANT_NEIGHS =
      {{-1, 1}, {0, 2}, {2, 1}, {2, -1}, {0, -2}, {-1, -1}};
  protected static final int[][] EVEN_NEAR_NEIGHS =
      {{-1, 0}, {-1, 1}, {0, 1}, {1, 0}, {0, -1}, {-1, -1}};
  protected static final int[][] EVEN_DISTANT_NEIGHS =
      {{-2, 1}, {0, 2}, {1, 1}, {1, -1}, {0, -2}, {-2, -1}};
  private static final double SQRT_3 = Math.sqrt(3);

  private Set<Position> frontier = new HashSet<>();

  private final AutomatonCell[][] cells;
  private final Function<Double, Double> phi;
  private final double Rmax;
  private final double timeStep;

  public ForestCellularAutomaton1(double[][] spreadRates, double[][][] winds, double[][] heights,
      double[][] ignitions, Function<Double, Double> phi, double L) {
    if (winds.length != heights.length || heights.length != spreadRates.length
        || winds[0].length != heights[0].length || heights[0].length != spreadRates[0].length
        || winds.length != ignitions.length || winds[0].length != ignitions[0].length) {
      throw new IllegalArgumentException("Matrices sizes don't match.");
    }
    this.cells = new AutomatonCell[winds.length][winds[0].length];
    for (int row = 0; row < cells.length; row++) {
      for (int col = 0; col < cells[0].length; col++) {
        cells[row][col] = new AutomatonCell(spreadRates[row][col], winds[row][col],
            heights[row][col], ignitions[row][col]);
        if (ignitions[row][col] > 0) {
          frontier.addAll(getNeighs(new Position(row, col)));
          if (ignitions[row][col] < 1) {
            frontier.add(new Position(row, col));
          }
        }
      }
    }

    this.phi = phi;
    this.Rmax = Arrays.stream(cells)
        .mapToDouble(
            row -> Arrays.stream(row).mapToDouble(cell -> cell.getFireSpread()).max().getAsDouble())
        .max().getAsDouble();
    this.timeStep = SQRT_3 * L / Rmax;
  }

  @Override
  public boolean next() {
    double[][] newBurnt = new double[cells.length][cells[0].length];
    List<Position> newFrontier = new ArrayList<>();
    List<Position> toRemove = new ArrayList<>();
    for (Position pos : frontier) {
      AutomatonCell cell = getAutomatonCell(pos.row, pos.col);
      if (cell.burnt == 1 || cell instanceof VoidCell) {
        toRemove.add(pos);
      } else {
        newBurnt[pos.row][pos.col] = nextBurntState(pos.row, pos.col);
        newFrontier.addAll(getNeighs(pos));
      }
    }
    frontier.removeAll(toRemove);
    for (Position pos : frontier) {
      AutomatonCell cell = getAutomatonCell(pos.row, pos.col);
      cell.setBurnt(Math.max(newBurnt[pos.row][pos.col], cell.getBurnt()));
    }
    frontier.addAll(newFrontier);
    return !frontier.isEmpty();
  }

  @Override
  public Cell getCell(int i, int j) {
    return new Cell(cells[i][j].getBurnt());
  }
  
  public double getBurntArea() {
    return getBurntArea(0, 0, getHeight(), getWidth());
  }

  public double getBurntArea(int startRow, int startCol, int endRow, int endCol) {
    double burntArea = 0;
    for (int row = startRow; row < endRow; row++) {
      for (int col = startCol; col < endCol; col++) {
        burntArea += getCell(row, col).getValue();
      }
    }
    return burntArea;
  }

  public AutomatonCell[][] getAutomatonCells() {
    return cells;
  }

  public double getTimeStep() {
    return timeStep;
  }

  public int getWidth() {
    return cells[0].length;
  }

  public int getHeight() {
    return cells.length;
  }

  private final double nextBurntState(int i, int j) {
    double sum = getAutomatonCell(i, j).getBurnt();
    for (int index = 0; index < EVEN_NEAR_NEIGHS.length; index++) {
      for (boolean isNear : new boolean[] {true, false}) {
        double burnt = getAutomatonCell(i, j, getNeighDelta(j, index, isNear)).getBurnt();
        if (burnt != 0) {
          double wind = getAutomatonCell(i, j).getWind(index, isNear);
          double coef = getCoef(i, j, index, isNear) * wind;
          sum += coef * burnt;
        }
      }
    }
    return discretize(sum);
  }

  private final double getCoef(int i, int j, int neighIndex, boolean isNear) {
    double r = getFireSpreadCoef(i, j, neighIndex, isNear);
    double h = getHeightCoef(i, j, neighIndex, isNear);
    return r * h;
  }

  protected final double getFireSpreadCoef(int i, int j, int neighIndex, boolean isNear) {
    if (getAutomatonCell(i, j).getFireSpread() == 0
        || getAutomatonCell(i, j, getNeighDelta(j, neighIndex, isNear))
            .getFireSpread() == Double.MIN_VALUE) {
      return 0;
    }
    if (isNear) {
      return getNearFireSpreadCoef(i, j);
    }
    return getDistantFireSpreadCoef(i, j, neighIndex);
  }

  private final double getDistantFireSpreadCoef(int i, int j, int neighIndex) {
    int[][] associateNeighDeltas = getAssociateNeighsDeltas(j, neighIndex);
    double maxR = Math.max(getAutomatonCell(i, j, associateNeighDeltas[0]).getFireSpread(),
        getAutomatonCell(i, j, associateNeighDeltas[1]).getFireSpread());
    double Rij = getAutomatonCell(i, j).getFireSpread();
    double a = 2.0 * Math.PI * SQRT_3 / 27.0;
    double b = SQRT_3 * Rij / Rmax;
    double c = Rij / maxR;
    double d = Math.max(0, b - c);
    // double d = b - c;
    return a * d * d;
  }

  private final double getNearFireSpreadCoef(int i, int j) {
    double Rij = getAutomatonCell(i, j).fireSpread;
    double condR = SQRT_3 * Rij;
    if (condR <= Rmax) {
      double a = 2.0 * SQRT_3 * Rij / (9.0 * Rmax);
      double b = Math.PI / 2.0 * Rij / Rmax;
      return a * (SQRT_3 + b);
    } else {
      double a = SQRT_3 / 4.0 * Rmax / Rij;
      double b = SQRT_3 * Rmax / Rij;
      double c = Math.sqrt(12.0 - b * b);
      double d = SQRT_3 / 12.0 * c;
      double alpha = Math.PI / 6.0 - Math.acos(a + d);
      double e = 2.0 * Rij / (3.0 * Rmax);
      double f = Math.sin(Math.PI / 6.0 - alpha);
      double g = SQRT_3 * alpha * Rij / Rmax;
      return e * (1.0 + f + g);
    }
  }

  protected double getHeightCoef(int i, int j, int neighIndex, boolean isNear) {
    if (isNear) {
      double Hij = getAutomatonCell(i, j).getHeight();
      double Hab = getAutomatonCell(i, j, getNeighDelta(j, neighIndex, true)).getHeight();
      if (Hab == Double.MIN_VALUE) {
        return 0;
      }
      return phi.apply(Hij - Hab);
    }
    return getDistantHeightCoef(i, j, neighIndex);
  }

  private double getDistantHeightCoef(int i, int j, int neighIndex) {
    int[][] associateNeighDeltas = getAssociateNeighsDeltas(j, neighIndex);
    double Hij = getAutomatonCell(i, j).getHeight();
    double Hab = getAutomatonCell(i, j, getNeighDelta(j, neighIndex, false)).getHeight();
    double Hd1 = getAutomatonCell(i, j, associateNeighDeltas[0]).getHeight();
    double Hd2 = getAutomatonCell(i, j, associateNeighDeltas[1]).getHeight();
    if (Hab == Double.MIN_VALUE || Hd1 == Double.MIN_VALUE || Hd2 == Double.MIN_VALUE) {
      return 0;
    }
    double a = phi.apply(Hij - Hd1);
    double b = phi.apply(Hd1 - Hab);
    double c = phi.apply(Hij - Hd2);
    double d = phi.apply(Hd2 - Hab);
    return (a + b + c + d) / 4;
  }

  protected double getWindCoef(int i, int j, int neighIndex, boolean isNear) {
    return getAutomatonCell(i, j, getNeighDelta(j, neighIndex, isNear)).getWind(neighIndex, isNear);
  }

  private static final int[] getNeighDelta(int j, int neighIndex, boolean isNear) {
    if (j % 2 == 0) {
      return isNear ? EVEN_NEAR_NEIGHS[neighIndex] : EVEN_DISTANT_NEIGHS[neighIndex];
    } else {
      return isNear ? ODD_NEAR_NEIGHS[neighIndex] : ODD_DISTANT_NEIGHS[neighIndex];
    }
  }

  protected static final int[][] getAssociateNeighsDeltas(int j, int neighIndex) {
    return Arrays.stream(getAssociateNeighs(neighIndex))
        .<int[]>mapToObj(associateNeighIndex -> (int[]) getNeighDelta(j, associateNeighIndex, true))
        .toArray(int[][]::new);

  }

  protected static final int[] getAssociateNeighs(int neighIndex) {
    return new int[] {neighIndex, (neighIndex + 1) % 6};
  }

  private final AutomatonCell getAutomatonCell(int i, int j, int[] delta) {
    return getAutomatonCell(i + delta[0], j + delta[1]);
  }

  protected final AutomatonCell getAutomatonCell(int i, int j) {
    if (i < 0 || i >= cells.length || j < 0 || j >= cells[0].length) {
      return VoidCell.get();
    }
    return cells[i][j];
  }

  private List<Position> getNeighs(Position pos) {
    List<Position> neighs = new ArrayList<>();
    for (int index = 0; index < EVEN_NEAR_NEIGHS.length; index++) {
      for (boolean isNear : new boolean[] {true, false}) {
        int[] delta = getNeighDelta(pos.col, index, isNear);
        neighs.add(new Position(pos.row + delta[0], pos.col + delta[1]));
      }
    }
    return neighs;
  }

  protected static final double discretize(double x) {
    return Math.max(Math.min(Math.round(10 * x) / 10.0, 1), 0);
  }

  public static class AutomatonCell {
    private double fireSpread;
    private double[] wind;
    private double height;
    private double burnt;

    public AutomatonCell(double fireSpread, double[] wind, double height, double burnt) {
      super();
      this.fireSpread = fireSpread;
      this.wind = wind;
      this.height = height;
      this.burnt = burnt;
    }

    public void setFireSpread(double fireSpread) {
      this.fireSpread = fireSpread;
    }

    public void setBurnt(double burnt) {
      this.burnt = burnt;
    }

    public double getFireSpread() {
      return fireSpread;
    }

    public double getWind(int index, boolean isNear) {
      return wind[(isNear ? 0 : 6) + index];
    }

    public double getHeight() {
      return height;
    }

    public double getBurnt() {
      return burnt;
    }
  }

  protected static class VoidCell extends AutomatonCell {

    private static VoidCell instance;

    static VoidCell get() {
      if (instance == null) {
        instance = new VoidCell();
      }
      return instance;
    }

    private VoidCell() {
      super(Double.MIN_VALUE, wind(), Double.MIN_VALUE, 0);
    }

    private static double[] wind() {
      double[] wind = new double[12];
      for (int i = 0; i < wind.length; i++) {
        wind[i] = Double.MIN_VALUE;
      }
      return wind;
    }
  }
  
  private class Position {
    int row;
    int col;

    public Position(int row, int col) {
      super();
      this.row = row;
      this.col = col;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + getOuterType().hashCode();
      result = prime * result + col;
      result = prime * result + row;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Position other = (Position) obj;
      if (!getOuterType().equals(other.getOuterType()))
        return false;
      if (col != other.col)
        return false;
      if (row != other.row)
        return false;
      return true;
    }

    private ForestCellularAutomaton1 getOuterType() {
      return ForestCellularAutomaton1.this;
    }
  }
}
