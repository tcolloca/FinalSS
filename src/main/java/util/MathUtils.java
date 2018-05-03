package util;

public class MathUtils {

  public static double lerp(double value, double minSrc, double maxSrc, double minDst,
      double maxDst) {
    double m = (maxDst - minDst) / (maxSrc - minSrc);
    return m * value + (maxDst - m * maxSrc);
  }
  
  public static void main(String[] args) {
    System.out.println(lerp(0.5, 0, 1, 20, 30));
  }
}
