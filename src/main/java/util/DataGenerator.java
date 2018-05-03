package util;

import java.util.Iterator;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import model.Direction;

public class DataGenerator {

  public static double[][][] randomWind(int width, int height, double minSpeed, double maxSpeed) {
    Random rand = new Random();
    Iterator<Double> it = rand.doubles(minSpeed, maxSpeed).iterator();
    return generateWind(width, height, (row, col) -> (neigh) -> it.next());
  }

  public static double[][][] randomLinearWindFront(int width, int height, Direction direction,
      double minSpeed, double maxSpeed) {
    Random rand = new Random();
    Iterator<Double> it = rand.doubles(minSpeed, maxSpeed).iterator();
    return linearWindFront(width, height, direction, (row, col) -> it.next());
  }

  public static double[][][] constantWind(int width, int height, double speed) {
    return generateWind(width, height, (row, col) -> neigh -> speed);
  }

  public static double[][][] exampleFunctionWind(int width, int height) {
    return linearWindFront(width, height, Direction.S,
        (row, col) -> height - row < 150 * Math.exp(-Math.pow(col - ((width - 1) / 2), 2) / width)
            ? 100.0
            : 0.005);
  }

  /**
   * @param width
   * @param height
   * @param direction
   * @param speedFunction (row, col) -> speed
   * @return
   */
  public static double[][][] linearWindFront(int width, int height, Direction direction,
      BiFunction<Integer, Integer, Double> speedFunction) {
    double[] wind;
    switch (direction) {
      case E:
        wind = new double[] {0, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0};
        break;
      case N:
        wind = new double[] {1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1};
        break;
      case NE:
        wind = new double[] {1, 1, 0.5, 0, 0, 0.5, 1, 1, 0, 0, 0, 0};
        break;
      case NW:
        wind = new double[] {1, 0.5, 0, 0, 0.5, 1, 0, 0, 0, 0, 1, 1};
        break;
      case S:
        wind = new double[] {0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0};
        break;
      case SE:
        wind = new double[] {0, 0.5, 1, 1, 0.5, 0, 0, 1, 1, 0, 0, 0};
        break;
      case SW:
        wind = new double[] {0, 0, 0.5, 1, 1, 0.5, 0, 0, 0, 1, 1, 0};
        break;
      case W:
        wind = new double[] {0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1};
        break;
      default:
        throw new IllegalArgumentException();
    }
    return generateWind(width, height, (row, col) -> (neigh) -> {
      double speed = speedFunction.apply(row, col);
      double windSpeed = wind[neigh] * speedFunction.apply(row, col);
      return windSpeed == 0 ? (speed == 0 ? 0 : 1) : windSpeed;
    });
  }

  /**
   * @param width
   * @param height
   * @param windFunction (row, col, neigh) -> wind_speed
   */
  public static double[][][] generateWind(int width, int height,
      BiFunction<Integer, Integer, Function<Integer, Double>> windFunction) {
    double[][][] winds = new double[height][width][12];
    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        for (int i = 0; i < 12; i++) {
          winds[row][col][i] = windFunction.apply(row, col).apply(i);
        }
      }
    }
    return winds;
  }

  public static double[][] randomPerlinHeight(int width, int height, int octaves, double lacunarity,
      double persistance, double min, double max) {
    return randomPerlinMatrix(width, height, octaves, lacunarity, persistance, min, max);
  }

  public static double[][] randomHeight(int width, int height, double min, double max) {
    return randomMatrix(width, height, min, max);
  }

  public static double[][] constantHeight(int width, int height, double heightValue) {
    return constantMatrix(width, height, heightValue);
  }

  public static double[][] linearHeight(int width, int height, Direction direction, double step) {
    return linearMatrix(width, height, direction, step);
  }

  /**
   * @param width
   * @param height
   * @param heightFunction (row, col) -> height
   */
  public static double[][] generateHeight(int width, int height,
      BiFunction<Integer, Integer, Double> heightFunction) {
    return generateMatrix(width, height, heightFunction);
  }
  
  public static double[][] randomFirePerlinSpreadRate(int width, int height, int octaves,
      double lacunarity, double persistance, double min, double max) {
    return randomPerlinMatrix(width, height, octaves, lacunarity, persistance, min, max);
  }

  public static double[][] randomFireSpreadRate(int width, int height, double min, double max) {
    return randomMatrix(width, height, min, max);
  }

  public static double[][] constantFireSpreadRate(int width, int height, double rate) {
    return constantMatrix(width, height, rate);
  }

  public static double[][] linearFireSpreadRate(int width, int height, Direction direction) {
    return linearMatrix(width, height, direction, 1);
  }

  /**
   * @param width
   * @param height
   * @param fireSpreadRateFunction (row, col) -> height
   */
  public static double[][] generateFireSpreadRate(int width, int height,
      BiFunction<Integer, Integer, Double> fireSpreadRateFunction) {
    return generateMatrix(width, height, fireSpreadRateFunction);
  }

  public static double[][] randomPerlinMatrix(int width, int height, int octaves, double lacunarity,
      double persistance, double min, double max) {
    return generateMatrix(width, height, (row, col) -> NoiseUtils.perlin(width, height, row, col,
        octaves, lacunarity, persistance, min, max));
  }

  private static double[][] randomMatrix(int width, int height, double min, double max) {
    Random rand = new Random();
    Iterator<Double> it = rand.doubles(min, max).iterator();
    return generateMatrix(width, height, (row, col) -> it.next());
  }

  private static double[][] constantMatrix(int width, int height, double value) {
    return generateMatrix(width, height, (row, col) -> value);
  }

  private static double[][] linearMatrix(int width, int height, Direction direction, double step) {
    return generateMatrix(width, height, (row, col) -> {
      switch (direction) {
        case E:
          return (double) col * step;
        case N:
          return (double) -row * step;
        case NE:
          return (double) (-row + col) * step;
        case NW:
          return (double) (-row - col) * step;
        case S:
          return (double) row * step;
        case SE:
          return (double) (row + col) * step;
        case SW:
          return (double) (row - col) * step;
        case W:
          return (double) -col * step;
        default:
          throw new IllegalArgumentException();
      }
    });
  }

  private static double[][] generateMatrix(int width, int height,
      BiFunction<Integer, Integer, Double> function) {
    double[][] matrix = new double[height][width];
    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        matrix[row][col] = function.apply(row, col);
      }
    }
    return matrix;
  }
}
