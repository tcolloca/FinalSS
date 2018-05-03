package view.panes;

import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import model.ForestCellularAutomaton1;
import util.ColorUtils;
import util.MathUtils;
import util.VectorUtils;
import util.observable.Observable;

public class CellsPane extends AnchorPane {

  private static final double SQRT_3 = Math.sqrt(3);
  private static final Color[] COLORS = {Color.valueOf("#0F0"), Color.valueOf("#4F0"),
      Color.valueOf("#8F0"), Color.valueOf("#BF0"), Color.valueOf("#DF0"), Color.valueOf("#FF0"),
      Color.valueOf("#FD0"), Color.valueOf("#FB0"), Color.valueOf("#F80"), Color.valueOf("#F40"),
      Color.valueOf("#F00")};

  private static double L = 2;
  private static double BORDER = 0;

  private final ForestCellularAutomaton1 forest;
  private final double pixelWidth;
  private final double pixelHeight;
  private final Observable<Boolean> paintTerrain;
  private final double minHeight;
  private final double maxHeight;

  public CellsPane(ForestCellularAutomaton1 forest, Observable<Boolean> paintTerrain,
      double minHeight, double maxHeight) {
    this.forest = forest;
    this.paintTerrain = paintTerrain;
    this.minHeight = minHeight;
    this.maxHeight = maxHeight;
    double[] size = initialize();
    this.pixelHeight = size[0];
    this.pixelWidth = size[1];
  }

  private double[] initialize() {
    double maxX = 0;
    double maxY = 0;
    for (int row = 0; row < forest.getHeight(); row++) {
      for (int col = 0; col < forest.getWidth(); col++) {
        Polygon hexagon = getHexagon(row, col);
        paint(hexagon, paintTerrain.get() ? getHeightColor(row, col) : COLORS[0]);
        maxX = Math.max(maxX, hexagon.getPoints().get(6));
        maxY = Math.max(maxY, hexagon.getPoints().get(9));
        getChildren().add(hexagon);
      }
    }
    return new double[] {maxY, maxX};
  }

  public void draw() {
    long startTime = System.currentTimeMillis();
    boolean paintTerrainChanged = paintTerrain.hasChanged();
    for (int row = 0; row < forest.getHeight(); row++) {
      for (int col = 0; col < forest.getWidth(); col++) {
        double value = forest.getCell(row, col).getValue();
        if (value != 0) {
          update(row, col, value);
        } else if (paintTerrainChanged) {
          if (paintTerrain.get()) {
            update(row, col, getHeightColor(row, col));
          } else {
            update(row, col, 0);
          }
        }
      }
    }
    long endTime = System.currentTimeMillis();
    System.out.println(String.format("Finished drawing in %dms", (endTime - startTime)));
  }

  public double getPixelWidth() {
    return pixelWidth;
  }

  public double getPixelHeight() {
    return pixelHeight;
  }

  private void update(int row, int col, double burnt) {
    update(row, col, COLORS[(int) Math.round(burnt * 10)]);
  }

  private void update(int row, int col, Color color) {
    getChildren().remove(forest.getWidth() * row + col);
    Polygon hexagon = getHexagon(row, col);
    paint(hexagon, color);
    getChildren().add(forest.getWidth() * row + col, hexagon);
  }

  private Color getHeightColor(int row, int col) {
    double half = (minHeight + maxHeight) / 2;
    double height = forest.getAutomatonCells()[row][col].getHeight();
    if (height < half) {
      int r = (int) Math.round(MathUtils.lerp(height, minHeight, half, 0, 255));
      return Color.valueOf(ColorUtils.toHex(r, 0, 255));
    } else {
      int g = (int) Math.round(MathUtils.lerp(height, half, maxHeight, 0, 255));
      return Color.valueOf(ColorUtils.toHex(255, g, 255));
    }
  }

  private Polygon getHexagon(int row, int col) {
    double[] a = getPoint(row, col);
    Polygon hexagon = getHexagon(a);
    return hexagon;
  }

  private void paint(Polygon polygon, Color color) {
    polygon.setFill(color);
  }

  private double[] getPoint(int row, int col) {
    double[] a = new double[2];
    a[0] = (3.0 / 2.0 * L + BORDER) * col;
    if (col % 2 == 0) {
      a[1] = (SQRT_3 * L + BORDER) * (row + 0.5);
    } else {
      a[1] = (SQRT_3 * L + BORDER) * (row + 1);
    }
    return a;
  }

  public Polygon getHexagon(double[] a) {
    double[] b = VectorUtils.sum(a, new double[] {0.5 * L, -SQRT_3 / 2.0 * L});
    double[] c = VectorUtils.sum(b, new double[] {L, 0});
    double[] d = VectorUtils.sum(a, new double[] {2 * L, 0});
    double[] e = VectorUtils.sum(c, new double[] {0, SQRT_3 * L});
    double[] f = VectorUtils.sum(e, new double[] {-L, 0});
    Polygon hexagon = new Polygon();
    hexagon.getPoints().addAll(
        new Double[] {a[0], a[1], b[0], b[1], c[0], c[1], d[0], d[1], e[0], e[1], f[0], f[1]});
    return hexagon;
  }
}
