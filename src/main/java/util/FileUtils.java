package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileUtils {
  
  private static final String DEL1 = "\r\n";
  private static final String DEL2 = ";";
  private static final String DEL3 = " ";

  public static void saveWinds(String path, double[][][] winds) throws IOException {
    String content = Arrays.stream(winds)
        .map(row -> Arrays.stream(row)
            .map(col -> Arrays.stream(col)
                .mapToObj(neigh -> String.valueOf(neigh))
                .reduce((n1, n2) -> n1 + DEL3 + n2).get())
            .reduce((s1, s2) -> s1 + DEL2 + s2).get())
        .reduce((s1, s2) -> s1 + DEL1 + s2).get();
    Files.write(Paths.get(path), content.getBytes());
  }

  public static void saveHeights(String path, double[][] heights, double minHeight,
      double maxHeight) throws IOException {
    StringBuilder content = new StringBuilder();
    content.append(minHeight).append(DEL1);
    content.append(maxHeight).append(DEL1);
    content.append(Arrays.stream(heights)
        .map(row -> Arrays.stream(row)
            .mapToObj(col -> String.valueOf(col))
            .reduce((s1, s2) -> s1 + DEL2 + s2).get())
        .reduce((s1, s2) -> s1 + DEL1 + s2).get());
    Files.write(Paths.get(path), content.toString().getBytes());
  }
  
  public static void saveSpreadRates(String path, double[][] spreadRates) throws IOException {
    StringBuilder content = new StringBuilder();
    content.append(Arrays.stream(spreadRates)
        .map(row -> Arrays.stream(row)
            .mapToObj(col -> String.valueOf(col))
            .reduce((s1, s2) -> s1 + DEL2 + s2).get())
        .reduce((s1, s2) -> s1 + DEL1 + s2).get());
    Files.write(Paths.get(path), content.toString().getBytes());
  }
  
  public static double[][][] loadWinds(String path) throws IOException {
    return Files.readAllLines(Paths.get(path)).stream()
      .map(line -> Arrays.stream(line.split(DEL2))
          .map(cell -> Arrays.stream(cell.split(DEL3))
            .mapToDouble(str -> Double.parseDouble(str))
            .toArray())
          .toArray(double[][]::new))
      .toArray(double[][][]::new);
  }
  
  public static double[][] loadHeights(String path) throws IOException {
    return Files.readAllLines(Paths.get(path)).stream().skip(2)
      .map(line -> Arrays.stream(line.split(DEL2))
          .mapToDouble(cell -> Double.parseDouble(cell))
            .toArray())
      .toArray(double[][]::new);
  }
  
  public static double loadMinHeight(String path) throws IOException {
    return Double.parseDouble(Files.readAllLines(Paths.get(path)).get(0));
  }
  
  public static double loadMaxHeight(String path) throws IOException {
    return Double.parseDouble(Files.readAllLines(Paths.get(path)).get(1));
  }
  
  public static double[][] loadSpreadRates(String path) throws IOException {
    return Files.readAllLines(Paths.get(path)).stream()
      .map(line -> Arrays.stream(line.split(DEL2))
          .mapToDouble(cell -> Double.parseDouble(cell))
            .toArray())
      .toArray(double[][]::new);
  }
}
