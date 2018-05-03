package util;

public class ColorUtils {

  public static String toHex(int r, int g, int b) {
    return new StringBuilder().append(toLetter(r)).append(toLetter(g)).append(toLetter(b))
        .toString();
  }

  private static String toLetter(int value) {
    int first = value / 16;
    int last = value - first * 16;
    return new StringBuilder().append(toChar(first)).append(toChar(last)).toString();
  }

  private static char toChar(int value) {
    if (value < 10) {
      return (char) ('0' + value);
    } else {
      return (char) ('A' + (value - 10));
    }
  }
}
