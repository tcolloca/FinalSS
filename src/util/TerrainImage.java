package util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TerrainImage {

	private static final double[] sparceColor = {33, 254, 223};
	private static final double[] normalColor = {239, 252, 17};
	private static final double[] denseColor = {254, 65, 1};
	private static final double[] waterColor = {0, 0, 142};
	private static final double[] cityColor = {128, 0, 0};
	private static final double[] fireColor = {255, 0, 0};
	
	private int width;
	private int height;
	private final double[][] pVegs;
	private final double[][] pDens;
	private final int[][] S;
	private final double[][] Es;
	
	public TerrainImage(String path) throws IOException {
		File imageFile = new File(path);
		BufferedImage image = ImageIO.read(imageFile);
		width = image.getWidth();
		height = image.getHeight();
		pDens = new double[height][width];
		pVegs = new double[height][width];
		S = new int[height][width];
		Es = new double[height][width];
		
		for (int col = 0; col < width; col++) {
			for (int row = 0; row < height; row++) {
				int rgb = image.getRGB(col, row);
				int r = (0xFF0000 & rgb) >> 16;
				int g = (0x00FF00 & rgb) >> 8;
				int b = 0x0000FF & rgb;
				double[] color = new double[]{r, g, b};
				
				double sparceDif = VectorUtils.dist(color, sparceColor);
				double normalDif = VectorUtils.dist(color, normalColor);
				double denseDif = VectorUtils.dist(color, denseColor);
				double waterDif = VectorUtils.dist(color, waterColor);
				double cityDif = VectorUtils.dist(color, cityColor);
				double fireDif = VectorUtils.dist(color, fireColor);
				double min = getMin(sparceDif, normalDif, denseDif, waterDif, fireDif, cityDif);
				
				if (min == cityDif || min == waterDif) {
					S[row][col] = 1;
				} else if (min == fireDif) {
					S[row][col] = 3;
				} else {
					S[row][col] = 2;
				}
				
				if (min == sparceDif) {
					pDens[row][col] = -0.4;
				} else if (min == denseDif) {
					pDens[row][col] = 0.3;
				}
			}
		}
	}

	private double getMin(double sparceDif, double normalDif, double denseDif,
			double waterDif, double fireDif, double cityDif) {
		return Math.min(sparceDif, Math.min(normalDif, Math.min(denseDif, Math.min(waterDif, 
				Math.min(fireDif, cityDif)))));
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public double[][] getpVegs() {
		return pVegs;
	}

	public double[][] getpDens() {
		return pDens;
	}

	public int[][] getS() {
		return S;
	}

	public double[][] getEs() {
		return Es;
	}
}
