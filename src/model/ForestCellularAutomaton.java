package model;

import java.util.Random;


public class ForestCellularAutomaton {

	private static final double COEF = 2 * (Math.sqrt(2) - 1);
	private static final double M = -0.8;
	private static final double B = 1;
	
	private double[][] S;
	private final double[][] h;
	private double speed = 0.1;
	private double n;
	private double w;
	private double s;
	private double e;
	private double nw;
	private double sw;
	private double ne;
	private double se;
	
	public ForestCellularAutomaton(int width, int height) {
		S = new double[width][height];
		h = new double[width][height];
		n = 1.5;
		s = 0.1;
		w = 1.5; 
		e = 0.1;
		nw = 1.5;
		ne = 0.1;
		sw = 0.1;
		se = 0.1;
		
		Random rand = new Random();
		int r = 3;
		int c = 40;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (Math.sqrt(Math.pow(i - c, 2) +Math.pow(j - c, 2)) < r * r) {
					h[i][j] = Math.round((r * r - Math.sqrt(Math.pow(i - c, 2) +Math.pow(j - c, 2))) * 10 / (double) (r * r));
//					System.out.println(h[i][j]);
				}
			}
		}
	}
	
	public void next() {
		double[][] newS = new double[S.length][S[0].length];
		for (int i = 0; i < S.length; i++) {
			for (int j = 0; j < S[0].length; j++) {
				newS[i][j] = S[i][j]
						+ speed * (s * getNeigh(i, j, -1, 0)
						+ e * getNeigh(i, j, 0, -1)
						+ n * getNeigh(i, j, 1, 0)
						+ w * getNeigh(i, j, 0, 1)
						+ COEF * (se * getNeigh(i, j, -1, -1)
								+ sw * getNeigh(i, j, -1, 1)
								+ ne * getNeigh(i, j, 1, -1)
								+ nw * getNeigh(i, j, 1, 1)
								));
				newS[i][j] = Math.min(1, newS[i][j]);
			}
		}
		S = newS;
	}

	private double getNeigh(int i, int j, int offI, int offJ) {
		if (!isValid(i + offI, j + offJ)) {
			return 0;
		}
		return heightValue(i, j, offI, offJ) * S[i + offI][j + offJ];
	}

	private double heightValue(int i, int j, int offI, int offJ) {
		System.out.println(M * Math.abs((h[i][j] - h[i + offI][j + offJ])) + B);
		return M * Math.abs((h[i][j] - h[i + offI][j + offJ])) + B;
	}
	
	public Cell getCell(int i, int j) {
		if (S[i][j] > 0.5) {
			return new Cell(1);
		} else if (S[i][j] >= 2) {
			Direction direction = null;
			if (isValid(i - 1, j - 1) && S[i - 1][j - 1] == 1) {
				direction = Direction.SE;
			} else if (isValid(i - 1, j + 1) && S[i - 1][j + 1] == 1) {
				direction = Direction.SW;
			} else if (isValid(i + 1, j - 1) && S[i + 1][j - 1] == 1) {
				direction = Direction.NE;
			} else if (isValid(i + 1, j + 1) && S[i + 1][j + 1] == 1) {
				direction = Direction.NW;
			}
			return new Cell(S[i][j], direction);
		} else {
			return new Cell(0);
		}
	}
	
	private boolean isValid(int i, int j) {
		return i >= 0 && i < S.length && j >= 0 && j < S[0].length;
	}

	public void setCell(int i, int j, double value) {
		S[i][j] = value;
	}
}
