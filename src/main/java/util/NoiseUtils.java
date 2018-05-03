package util;

public class NoiseUtils {

  private static final OpenSimplexNoise simplexNoise =
      new OpenSimplexNoise(System.currentTimeMillis());

  public static double perlin(int width, int height, int row, int col, int octaves,
      double lacunarity, double persistance, double minValue, double maxValue) {
    double frequency = 1;
    double amplitude = 1;
    double maxAmplitude = 0;
    double value = 0;
    for (int i = 0; i < octaves; i++) {
      double x = col / (double) width * frequency;
      double y = row / (double) height * frequency;
      double perlin = simplexNoise.eval(x, y);


      if (perlin < -1 || perlin > 1) {
        throw new IllegalArgumentException("" + perlin);
      }
      value += perlin * amplitude;

      maxAmplitude += amplitude;
      frequency *= lacunarity;
      amplitude *= persistance;
    }
    if (value > maxAmplitude || value < -maxAmplitude) {
      throw new IllegalArgumentException(value + "  " + maxAmplitude);
    }
    return MathUtils.lerp(value, -maxAmplitude, maxAmplitude, minValue, maxValue);
  }
}
