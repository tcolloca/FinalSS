package util;

import java.io.IOException;
import java.util.Random;
import model.Direction;

public class MapGenerator {

  private static final int W = 101;
  private static final int H = 101;

  private final static Random rand = new Random();

  public static void main(String[] args) throws IOException {

    for (int i = 0; i < 1000; i++) {
      double[][][] winds = getRandomWinds();
      Heights heights = getRandomHeights();
      double[][] spreads = getRandomSpreadRates();

      FileUtils.saveWinds("maps/wind" + i + ".txt", winds);
      FileUtils.saveHeights("maps/height" + i + ".txt", heights.heights, heights.minHeight,
          heights.maxHeight);
      FileUtils.saveSpreadRates("maps/spread" + i + ".txt", spreads);
    }
  }

  private static Direction randomDirection() {
    return Direction.values()[rand.nextInt(8)];
  }

  private static double[][][] getRandomWinds() {
    double[][][] winds = null;
    double speed = rand.doubles(0.055, 5).findFirst().getAsDouble();
    double minSpeed = rand.doubles(0, 5).findFirst().getAsDouble();
    double maxSpeed = rand.doubles(minSpeed, 5).findFirst().getAsDouble();
    Direction direction = randomDirection();
    switch (rand.nextInt(4)) {
      case 0:
        return DataGenerator.constantWind(W, H, speed);
      case 1:
        return DataGenerator.linearWindFront(W, H, direction, (row, col) -> speed);
      case 2:
        return DataGenerator.randomWind(W, H, minSpeed, maxSpeed);
      case 3:
        return DataGenerator.randomLinearWindFront(W, H, direction, minSpeed, maxSpeed);
    }
    return winds;
  }

  private static Heights getRandomHeights() {
    double height = rand.doubles(0.1, 5000).findFirst().getAsDouble();
    double minHeight = rand.doubles(0, 10000).findFirst().getAsDouble();
    double maxHeight = rand.doubles(minHeight, 10000).findFirst().getAsDouble();
    int octaves = rand.nextInt(7) + 1;
    double lacunarity = rand.doubles(0.1, 4).findFirst().getAsDouble();
    double persistance = rand.doubles(0.1, 4).findFirst().getAsDouble();
    Direction direction = randomDirection();

    switch (rand.nextInt(4)) {
      case 0:
        return new Heights(0.1, 5000, DataGenerator.constantHeight(W, H, height));
      case 1:
        return new Heights((-W - H) * height, (W + H) * height,
            DataGenerator.linearHeight(W, H, direction, height));
      case 2:
        return new Heights(0, 10000, DataGenerator.randomHeight(W, H, minHeight, maxHeight));
      case 3:
        return new Heights(0, 10000, DataGenerator.randomPerlinHeight(W, H, octaves, lacunarity,
            persistance, minHeight, maxHeight));
    }
    return null;
  }

  private static double[][] getRandomSpreadRates() {
    double spreadRate = rand.doubles(0, 1).findFirst().getAsDouble();
    double minSpreadRate = rand.doubles(0, 1).findFirst().getAsDouble();
    double maxSpreadRate = rand.doubles(minSpreadRate, 1).findFirst().getAsDouble();
    int octaves = rand.nextInt(7) + 1;
    double lacunarity = rand.doubles(0.1, 4).findFirst().getAsDouble();
    double persistance = rand.doubles(0.1, 4).findFirst().getAsDouble();
    Direction direction = randomDirection();

    switch (rand.nextInt(4)) {
      case 0:
        return DataGenerator.constantFireSpreadRate(W, H, spreadRate);
      case 1:
        return DataGenerator.linearFireSpreadRate(W, H, direction);
      case 2:
        return DataGenerator.randomFireSpreadRate(W, H, minSpreadRate, maxSpreadRate);
      case 3:
        return DataGenerator.randomFirePerlinSpreadRate(W, H, octaves, lacunarity, persistance,
            minSpreadRate, maxSpreadRate);
    }
    return null;
  }

  private static class Heights {
    double minHeight;
    double maxHeight;
    double[][] heights;

    public Heights(double minHeight, double maxHeight, double[][] heights) {
      super();
      this.minHeight = minHeight;
      this.maxHeight = maxHeight;
      this.heights = heights;
    }
  }
}
