package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import util.TerrainImage;

public class ForestCellularAutomaton2 implements CellularAutomaton {
	
	private static final Random rand = new Random();
	private static final double sqrt2 = Math.sqrt(2);

	private final double pH;
	private final double[][] pVegs;
	private final double[][] pDens;
	private final double[][] Es;
	private final double c1;
	private final double c2;
	private final double a;
	private final double l;
	
	private double windSpeed;
	private double windAngle;

	private int[][] S;
	
	public ForestCellularAutomaton2(TerrainImage terrainImage) {
		S = terrainImage.getS();
		pVegs = terrainImage.getpVegs();
		pDens = terrainImage.getpDens();
		Es = terrainImage.getEs();
		pH = 0.58; // TODO: 0.58
		c1 = 0.045;
		c2 = 0.131;
		a = 0.0758;
		this.l = 1;
		
		windSpeed = 10;
		windAngle = 0;
	}
	
	public boolean next() {
		int[][] newS = new int[S.length][S[0].length];
		boolean hasBurning = false;
		for (int i = 0; i < S.length; i++) {
			for (int j = 0; j < S[0].length; j++) {
				if (S[i][j] == 1 || S[i][j] == 4) {
					newS[i][j] = S[i][j];
				} else if (S[i][j] == 3) {
					newS[i][j] = 4;
					for (Point neigh: burnableNeighs(i, j)) {
						double p = rand.nextDouble();
						if (p <= burnProbability(i, j, neigh.x, neigh.y)) {
							newS[neigh.x][neigh.y] = 3;
							hasBurning = true;
						}
					}
				} else if (newS[i][j] == 0) {
					newS[i][j] = 2;
				}
			}
		}
		S = newS;
		return hasBurning;
	}
	
	private double burnProbability(int originI, int originJ, int i, int j) {
		return pH * (1 + pVegs[i][j]) * (1 + pDens[i][j]) * pW(originI, originJ, i, j) * pS(originI, originJ, i, j);
	}
	
	private double pW(int originI, int originJ, int i, int j) {
		int yDist = originI - i;
		int xDist = j - originJ;
		double angle;
		if (xDist > 0 && yDist == 0) {
			angle = 0;
		} else if (xDist > 0 && yDist > 0) {
			angle = 45;
		} else if (xDist == 0 && yDist > 0) {
			angle = 90;
		} else if (xDist < 0 && yDist > 0) {
			angle = 135;
		} else if (xDist < 0 && yDist == 0) {
			angle = 180;
		}  else if (xDist < 0 && yDist < 0) {
			angle = 225;
		} else if (xDist == 0 && yDist < 0) {
			angle = 270;
		} else {
			angle = 315;
		}
		double difAngle = Math.abs(windAngle - angle);
		System.out.println("pw: " + Math.exp(c1 * windSpeed) * Math.exp(c2 * windSpeed * Math.cos(difAngle * Math.PI / 180.0)));
		return Math.exp(c1 * windSpeed) * Math.exp(c2 * windSpeed * Math.cos(difAngle * Math.PI / 180.0));
	}
	
	private double pS(int originI, int originJ, int i, int j) {
		double xDist = originI - i != 0 && originJ - j != 0 ? sqrt2 * l : l;
		double slopeAngle = Math.atan((Es[originI][originJ] - Es[i][j]) / xDist);
		if (slopeAngle < 0) {
			System.err.println("WARNING: SLOPE NEGATIVE");
		}
		System.out.println("ps: " + Math.exp(a * slopeAngle));
		return Math.exp(a * slopeAngle);
	}

	private List<Point> burnableNeighs(int x, int y) {
		List<Point> points = new ArrayList<>();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (i == 0 && j == 0) {
					continue;
				}
				if (isValid(x + i, y + j) && S[x + i][y + j] == 2) {
					points.add(new Point(x + i, y + j));
				}
			}
		}
		return points;
	}
	
	private boolean isValid(int i, int j) {
		return i >= 0 && i < S.length && j >= 0 && j < S[0].length;
	}
	
	public void setCell(int i, int j, int value) {
		S[i][j] = value;
	}
	
	private static class Point {
		int x;
		int y;

		public Point(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}
	}

	@Override
	public Cell getCell(int i, int j) {
		return new Cell(S[i][j] >= 3 ? 1 : 0);
	}
}
