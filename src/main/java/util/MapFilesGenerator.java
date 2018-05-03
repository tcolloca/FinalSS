package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import model.Direction;
import model.ForestCellularAutomaton1;

public class MapFilesGenerator {

  private static final int W = 101;
  private static final int H = 101;
  private static final double REAL_L = 1;

  private static final int MAX_ITERATIONS = 500;
  private static final int MAX_MAPS = 1000;

  private static final double[] WIND_DATA = {0.055, 0.5, 1, 5};

  private static final double[] HEIGHT_DATA = {1, 50, 500, 5000};

  private static final double[] SPREAD_DATA = {0.1, 0.33, 0.67, 1};

  private static ForestCellularAutomaton1 forest;

  private static double[][] ignitions;
  private static Function<Double, Double> phi;

  public static void main(String[] args) throws IOException {
    long start = System.currentTimeMillis();
    ignitions = new double[H][W];
    ignitions[(H - 1) / 2][(W - 1) / 2] = 1;

    phi = x -> {
      if (x > 0) {
        return 1 + Math.sqrt(x) / 10;
      } else {
        return Math.exp(x);
      }
    };
    StandardDeviation stdCalc = new StandardDeviation();
    Mean meanCalc = new Mean();
    
    for (double value : WIND_DATA) {
      double[][][] data = new double[MAX_ITERATIONS + 1][3][MAX_MAPS];  
      for (int i = 0; i < MAX_MAPS; i++) {
        if (i % 10 == 0) {
          System.out.println(String.format("%.2f%%", i / 10.0));
        }
        double[][] heights = FileUtils.loadHeights("maps/height" + i + ".txt");
        double[][] spreadRates = FileUtils.loadSpreadRates("maps/spread" + i + ".txt");
        double[][] mapData = timeRun(spreadRates, DataGenerator.constantWind(W, H, value), heights);
        for (int t = 0; t <= MAX_ITERATIONS; t++) {
          data[t][0][i] = mapData[t][0];
          data[t][1][i] = mapData[t][1];
          data[t][2][i] = mapData[t][2];
        }
      }
      StringBuilder strBuilder = new StringBuilder();
      for (int t = 0; t <= MAX_ITERATIONS; t++) {
        double mean0 = meanCalc.evaluate(data[t][0]);
        double std0 = stdCalc.evaluate(data[t][0], mean0);
        double mean1 = meanCalc.evaluate(data[t][1]);
        double std1 = stdCalc.evaluate(data[t][1], mean1);
        double mean2 = meanCalc.evaluate(data[t][2]);
        double std2 = stdCalc.evaluate(data[t][2], mean2);
        
        strBuilder.append(t);
        strBuilder.append(",");
        strBuilder.append(String.format("%.5f", mean0));
        strBuilder.append(",");
        strBuilder.append(String.format("%.5f", std0));
        strBuilder.append(",");
        strBuilder.append(String.format("%.5f", mean1));
        strBuilder.append(",");
        strBuilder.append(String.format("%.5f", std1));
        strBuilder.append(",");
        strBuilder.append(String.format("%.5f", mean2));
        strBuilder.append(",");
        strBuilder.append(String.format("%.5f", std2));
        strBuilder.append("\r\n");
      }
      Files.write(Paths.get("output_maps/wind_" + value + ".csv"), strBuilder.toString().getBytes());
    }

    for (double value : HEIGHT_DATA) {
      double[][][] data = new double[MAX_ITERATIONS + 1][3][MAX_MAPS];  
      for (int i = 0; i < MAX_MAPS; i++) {
        if (i % 10 == 0) {
          System.out.println(String.format("%.2f%%", i / 10.0));
        }
        double[][][] winds = FileUtils.loadWinds("maps/wind" + i + ".txt");
        double[][] spreadRates = FileUtils.loadSpreadRates("maps/spread" + i + ".txt");
        double[][] mapData =
            timeRun(spreadRates, winds, DataGenerator.linearHeight(W, H, Direction.N, value));
        for (int t = 0; t <= MAX_ITERATIONS; t++) {
          data[t][0][i] = mapData[t][0];
          data[t][1][i] = mapData[t][1];
          data[t][2][i] = mapData[t][2];
        }
      }
      StringBuilder strBuilder = new StringBuilder();
      for (int t = 0; t <= MAX_ITERATIONS; t++) {
        double mean0 = meanCalc.evaluate(data[t][0]);
        double std0 = stdCalc.evaluate(data[t][0], mean0);
        double mean1 = meanCalc.evaluate(data[t][1]);
        double std1 = stdCalc.evaluate(data[t][1], mean1);
        double mean2 = meanCalc.evaluate(data[t][2]);
        double std2 = stdCalc.evaluate(data[t][2], mean2);
        
        strBuilder.append(t);
        strBuilder.append(",");
        strBuilder.append(String.format("%.5f", mean0));
        strBuilder.append(",");
        strBuilder.append(String.format("%.5f", std0));
        strBuilder.append(",");
        strBuilder.append(String.format("%.5f", mean1));
        strBuilder.append(",");
        strBuilder.append(String.format("%.5f", std1));
        strBuilder.append(",");
        strBuilder.append(String.format("%.5f", mean2));
        strBuilder.append(",");
        strBuilder.append(String.format("%.5f", std2));
        strBuilder.append("\r\n");
      }
      Files.write(Paths.get("output_maps/height_" + value + ".csv"), strBuilder.toString().getBytes());
    }

    for (double value : SPREAD_DATA) {
      double[][][] data = new double[MAX_ITERATIONS + 1][3][MAX_MAPS];  
      for (int i = 0; i < MAX_MAPS; i++) {
        if (i % 10 == 0) {
          System.out.println(String.format("%.2f%%", i / 10.0));
        }
        double[][][] winds = FileUtils.loadWinds("maps/wind" + i + ".txt");
        double[][] heights = FileUtils.loadHeights("maps/height" + i + ".txt");
        double[][] mapData =
            timeRun(DataGenerator.constantFireSpreadRate(W, H, value), winds, heights);
        for (int t = 0; t <= MAX_ITERATIONS; t++) {
          data[t][0][i] = mapData[t][0];
          data[t][1][i] = mapData[t][1];
          data[t][2][i] = mapData[t][2];
        }
      }
      StringBuilder strBuilder = new StringBuilder();
      for (int t = 0; t <= MAX_ITERATIONS; t++) {
        double mean0 = meanCalc.evaluate(data[t][0]);
        double std0 = stdCalc.evaluate(data[t][0], mean0);
        double mean1 = meanCalc.evaluate(data[t][1]);
        double std1 = stdCalc.evaluate(data[t][1], mean1);
        double mean2 = meanCalc.evaluate(data[t][2]);
        double std2 = stdCalc.evaluate(data[t][2], mean2);
        
        strBuilder.append(t);
        strBuilder.append(",");
        strBuilder.append(String.format("%.5f", mean0));
        strBuilder.append(",");
        strBuilder.append(String.format("%.5f", std0));
        strBuilder.append(",");
        strBuilder.append(String.format("%.5f", mean1));
        strBuilder.append(",");
        strBuilder.append(String.format("%.5f", std1));
        strBuilder.append(",");
        strBuilder.append(String.format("%.5f", mean2));
        strBuilder.append(",");
        strBuilder.append(String.format("%.5f", std2));
        strBuilder.append("\r\n");
      }
      Files.write(Paths.get("output_maps/spread_" + value + ".csv"), strBuilder.toString().getBytes());
    }
    long end = System.currentTimeMillis();
    System.out.println(end - start);
  }

  private static double[][] timeRun(double[][] spreadRates, double[][][] winds,
      double[][] heights) {
    spreadRates[(H - 1) / 2][(W - 1) / 2] = 1;
    forest = new ForestCellularAutomaton1(spreadRates, winds, heights, ignitions, phi, REAL_L);
    double burntArea = 0;
    double prevBurntArea = Double.MIN_VALUE;
    int t = 0;
    double[][] data = new double[MAX_ITERATIONS + 1][3];
    do {
      data[t][0] = burntArea();
      data[t][1] = topBurntArea();
      data[t][2] = bottomBurntArea();
      prevBurntArea = burntArea;
      forest.next();
      burntArea = burntArea();
    } while ((burntArea != prevBurntArea || t < 10) && t++ < MAX_ITERATIONS);
    return data;
  }

  public static double burntArea() {
    return burntArea(2, 2, H - 2, W - 2);
  }

  public static double bottomBurntArea() {
    return burntArea((H - 1) / 2 + 2, 2, H - 2, W - 2);
  }

  public static double topBurntArea() {
    return burntArea(2, 2, (H - 1) / 2 - 1, W - 2);
  }

  public static double burntArea(int startRow, int startCol, int endRow, int endCol) {
    double burntArea = 0;
    for (int row = startRow; row < endRow; row++) {
      for (int col = startCol; col < endCol; col++) {
        burntArea += forest.getCell(row, col).getValue();
      }
    }
    return burntArea;
  }
}
