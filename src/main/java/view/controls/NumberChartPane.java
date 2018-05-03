package view.controls;

import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import util.observable.ObservableList;

public class NumberChartPane extends LineChart<Number, Number> {

  public NumberChartPane(ObservableList<Number[]> dataObs, String xLabel, String yLabel) {
    super(getAxis(xLabel), getAxis(yLabel));

    setLegendVisible(false);

    XYChart.Series<Number, Number> series = new XYChart.Series<>();

    setAnimated(false);

    series.getData()
        .addAll(dataObs.get().stream().map(arr -> getEntry(arr)).collect(Collectors.toList()));

    getData().add(series);

    dataObs.addListener(
        (list, newVal) -> Platform.runLater(() -> {
          series.getData().add(getEntry(newVal));
          for (XYChart.Data<Number, Number> entry : series.getData()) {
             StackPane pane = (StackPane) entry.getNode();
             if (pane != null) {
               pane.setVisible(false);
             }
          }
        }));
  }

  private static NumberAxis getAxis(String label) {
    final NumberAxis axis = new NumberAxis();
    axis.setAnimated(false);
    axis.setLabel(label);
    return axis;
  }

  private static XYChart.Data<Number, Number> getEntry(Number[] arr) {
    XYChart.Data<Number, Number> entry = new XYChart.Data<>(arr[0], arr[1]);
    return entry;
  }
}
