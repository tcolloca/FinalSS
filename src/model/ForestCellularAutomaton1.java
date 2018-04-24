package model;

import java.util.Arrays;

public class ForestCellularAutomaton1 implements CellularAutomaton {

	private static final int[][] ODD_CLOSE_NEIGHS = { { -1, 0 }, { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 }, { 0, -1 } };
	private static final int[][] ODD_FAR_NEIGHS = { { -1, 1 }, { 0, 2 }, { 2, 1 }, { 2, -1 }, { 0, -2 }, { -1, -1 } };
	private static final int[][] EVEN_CLOSE_NEIGHS = { {-1, 0}, {-1, 1}, {0, 1}, {1, 0}, {0, -1}, {-1, -1} };
	private static final int[][] EVEN_FAR_NEIGHS = { {-2, 1}, {0, 2}, {1, 1}, {1, -1}, {0, -2}, {-2, -1} };
	private static final double L = 1;

	private final AutomatonCell[][] cells;

	public ForestCellularAutomaton1(AutomatonCell[][] cells) {
		this.cells = cells;
	}

	@Override
	public boolean next() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cell getCell(int i, int j) {
		return new Cell(cells[i][j].getBurnt());
	}
	
	private final double getCoef(int i, int j, int neighIndex, boolean isClose) {
		
	}
	
	private final double getFireSpreadCoef(int i, int j, int neighIndex, boolean isClose) {
		
	}
	
	private final double getFarFireSpreadCoef(int i, int j, int neighIndex, boolean isClose) {
	    int[][] associateNeighIndexes = Arrays.stream(getAssociateNeighs(neighIndex))
	    		.mapToObj(associateNeighIndex -> getNeighDelta(j, associateNeighIndex, !isClose)).toArray();
		double maxR = Math.max(getAutomatonCell(i, j, associateNeighIndexes[0]).getFireSpread(), 
				getAutomatonCell(i, j, associateNeighIndexes[1]).getFireSpread());
	}
	
	private final int[] getNeighDelta(int j, int neighIndex, boolean isClose) {
		if (j % 2 == 0) {
			return isClose ? EVEN_CLOSE_NEIGHS[neighIndex] : EVEN_FAR_NEIGHS[neighIndex];
		} else {
			return isClose ? ODD_CLOSE_NEIGHS[neighIndex] : ODD_FAR_NEIGHS[neighIndex];
		}
	}
	
	private final int[] getAssociateNeighs(int neighIndex) {
		return new int[] { neighIndex, (neighIndex + 1) % 6};
	}
	
	private final AutomatonCell getAutomatonCell(int i, int j, int[] delta) {
		return getAutomatonCell(i + delta[0], j + delta[1]);
	}
	
	private final AutomatonCell getAutomatonCell(int i, int j) {
		if (i < 0 || i >= cells.length || j < 0 || j >= cells.length) {
			return null;
		}
		return cells[i][j];
	}
	
	private final double discretize(double x) {
		return Math.max(Math.min(Math.round(10 * x) / 10.0, 1), 0);
	}

	public static class AutomatonCell {
		private double fireSpread;
		private double wind;
		private double height;
		private double burnt;

		public double getFireSpread() {
			return fireSpread;
		}

		public double getWind() {
			return wind;
		}

		public double getHeight() {
			return height;
		}

		public double getBurnt() {
			return burnt;
		}
	}
}
