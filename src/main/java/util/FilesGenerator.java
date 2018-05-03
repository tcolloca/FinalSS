package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;
import model.Direction;
import model.ForestCellularAutomaton1;
import util.observable.Observable;

public class FilesGenerator {

  private static final int W = 201;
  private static final int H = 201;
  private static final double REAL_L = 1;

  private static final int MAX_ITERATIONS = 1000;

  private static final double WIND_MIN = 0;
  private static final double WIND_MAX = 5;
  private static final int WIND_POINTS = 250;
  private static final double WIND_STEP = (WIND_MAX - WIND_MIN) / WIND_POINTS;
//  private static final double[] WIND_DATA = {0.055, 0.5, 1, 5};
  private static final double[] WIND_DATA = {0.1};

  private static final double HEIGHT_MIN = 0;
  private static final double HEIGHT_MAX = 5000;
  private static final int HEIGHT_POINTS = 250;
  private static final double HEIGHT_STEP = (HEIGHT_MAX - HEIGHT_MIN) / HEIGHT_POINTS;
//  private static final double[] HEIGHT_DATA = {1, 50, 500, 5000};
  private static final double[] HEIGHT_DATA = {5};
  
  private static final double SPREAD_MIN = 0;
  private static final double SPREAD_MAX = 1;
  private static final int SPREAD_POINTS = 250;
  private static final double SPREAD_STEP = (SPREAD_MAX - SPREAD_MIN) / SPREAD_POINTS;
//  private static final double[] SPREAD_DATA = {0.1, 0.33, 0.67, 1};
  private static final double[] SPREAD_DATA = {0.2};


  private static Observable<double[][][]> defWindsObs =
      new Observable<double[][][]>(DataGenerator.constantWind(W, H, 1));
  private static Observable<double[][]> defHeightsObs =
      new Observable<>(DataGenerator.constantHeight(W, H, 1));
  private static Observable<double[][]> defSpreadRatesObs =
      new Observable<>(DataGenerator.constantFireSpreadRate(W, H, 1));

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

//    StringBuilder strBuilder;
//
//    strBuilder = new StringBuilder();
//    for (double value = WIND_MIN; value <= WIND_MAX; value += WIND_STEP) {
//      String row = dataRun(value, defSpreadRatesObs.get(), DataGenerator.constantWind(W, H, value),
//          defHeightsObs.get());
//      strBuilder.append(row);
//    }
//    Files.write(Paths.get("output/wind_time.csv"), strBuilder.toString().getBytes());
//    
//    strBuilder = new StringBuilder();
//    for (double value = HEIGHT_MIN; value <= HEIGHT_MAX; value += HEIGHT_STEP) {
//      String row = dataRun(value, defSpreadRatesObs.get(), defWindsObs.get(),
//          DataGenerator.linearHeight(W, H, Direction.N, value));
//      strBuilder.append(row);
//    }
//    Files.write(Paths.get("output/height_time.csv"), strBuilder.toString().getBytes());
//
//    strBuilder = new StringBuilder();
//    for (double value = SPREAD_MIN; value <= SPREAD_MAX; value += SPREAD_STEP) {
//      String row = dataRun(value, DataGenerator.constantFireSpreadRate(W, H, value),
//          defWindsObs.get(), defHeightsObs.get());
//      strBuilder.append(row);
//    }
//    Files.write(Paths.get("output/spread_time.csv"), strBuilder.toString().getBytes());

    for (double value : WIND_DATA) {
      String content = timeRun(defSpreadRatesObs.get(), DataGenerator.constantWind(W, H, value),
          defHeightsObs.get());
      Files.write(Paths.get("output/wind_" + value + ".csv"), content.getBytes());
    }
    
    for (double value : HEIGHT_DATA) {
      String content = timeRun(defSpreadRatesObs.get(), defWindsObs.get(),
          DataGenerator.linearHeight(W, H, Direction.N, value));
      Files.write(Paths.get("output/height_" + value + ".csv"), content.getBytes());
    }
    
    for (double value : SPREAD_DATA) {
      String content = timeRun(DataGenerator.constantFireSpreadRate(W, H, value), defWindsObs.get(),
          defHeightsObs.get());
      Files.write(Paths.get("output/spread_" + value + ".csv"), content.getBytes());
    }
    long end = System.currentTimeMillis();
    System.out.println(end - start);
  }

  private static String timeRun(double[][] spreadRates, double[][][] winds, double[][] heights) {
    StringBuilder vsTimeStrBuilder = new StringBuilder();
    spreadRates[(H - 1) / 2][(W - 1) / 2] = 1;
    forest = new ForestCellularAutomaton1(spreadRates, winds, heights, ignitions, phi, REAL_L);
    double burntArea = 0;
    double prevBurntArea = Double.MIN_VALUE;
    int t = 0;
    do {
      vsTimeStrBuilder.append(t);
      vsTimeStrBuilder.append(",");
      vsTimeStrBuilder.append(String.format("%.1f", burntArea()));
      vsTimeStrBuilder.append(",");
      vsTimeStrBuilder.append(String.format("%.1f", topBurntArea()));
      vsTimeStrBuilder.append(",");
      vsTimeStrBuilder.append(String.format("%.1f", bottomBurntArea()));
      vsTimeStrBuilder.append("\r\n");
      prevBurntArea = burntArea;
      forest.next();
      burntArea = burntArea();
    } while ((burntArea != prevBurntArea || t < 10) && t++ < MAX_ITERATIONS);
    return vsTimeStrBuilder.toString();
  }

  private static String dataRun(double value, double[][] spreadRates, double[][][] winds,
      double[][] heights) {
    StringBuilder strBuilder = new StringBuilder();
    spreadRates[(H - 1) / 2][(W - 1) / 2] = 1;
    forest = new ForestCellularAutomaton1(spreadRates, winds, heights, ignitions, phi, REAL_L);
    double burntArea = 0;
    double prevBurntArea = Double.MIN_VALUE;
    int t = 0;
    do {
      prevBurntArea = burntArea;
      forest.next();
      burntArea = burntArea();
    } while (burntArea != prevBurntArea && t++ < MAX_ITERATIONS);
    strBuilder.append(value);
    strBuilder.append(",");
    strBuilder.append(t);
    strBuilder.append(",");
    strBuilder.append(String.format("%.1f", burntArea()));
    strBuilder.append(",");
    strBuilder.append(String.format("%.1f", topBurntArea()));
    strBuilder.append(",");
    strBuilder.append(String.format("%.1f", bottomBurntArea()));
    strBuilder.append("\r\n");
    return strBuilder.toString();
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
