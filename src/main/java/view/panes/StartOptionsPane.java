package view.panes;

import java.io.File;
import java.io.IOException;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import util.FileUtils;
import util.observable.Observable;
import view.controls.FilePicker;
import view.controls.OpenWindowButton;
import view.controls.Window;

public class StartOptionsPane extends GridPane {

  private final double WIDTH = 400;

  private final Observable<File> windFileObs;
  private final Observable<File> heightFileObs;
  private final Observable<File> spreadRateFileObs;

  public StartOptionsPane(Observable<double[][][]> winds, Observable<double[][]> heights,
      Observable<Double> minHeightObs, Observable<Double> maxHeightObs,
      Observable<double[][]> spreadRates, Observable<Boolean> simulate) {
    windFileObs = new Observable<>();
    heightFileObs = new Observable<>();
    spreadRateFileObs = new Observable<>();

    windFileObs.addListener((File oldVal, File newVal) -> {
      try {
        winds.update(FileUtils.loadWinds(newVal.getAbsolutePath()));
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    heightFileObs.addListener((File oldVal, File newVal) -> {
      try {
        heights.update(FileUtils.loadHeights(newVal.getAbsolutePath()));
        minHeightObs.update(FileUtils.loadMinHeight(newVal.getAbsolutePath()));
        maxHeightObs.update(FileUtils.loadMaxHeight(newVal.getAbsolutePath()));
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    spreadRateFileObs.addListener((File oldVal, File newVal) -> {
      try {
        spreadRates.update(FileUtils.loadSpreadRates(newVal.getAbsolutePath()));
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    setPrefWidth(WIDTH);
    double buttonWidth = 120;
    FilePicker windFilePicker = new FilePicker(windFileObs, "Load Wind...", buttonWidth);
    FilePicker heightFilePicker = new FilePicker(heightFileObs, "Load Height...", buttonWidth);
    FilePicker spreadRateFilePicker =
        new FilePicker(spreadRateFileObs, "Load Spread Rate...", buttonWidth);
    Button simulateButton = new Button("Simulate");
    GridPane.setMargin(simulateButton, new Insets(0, 0, 0, 150));
    simulateButton.setOnAction((event) -> simulate.update(true));

    add(windFilePicker, 0, 0);
    add(new OpenWindowButton(new Window("Create Wind", new CreateWindPane(winds), 300, 300),
        "Create Wind..."), 1, 0);
    add(heightFilePicker, 0, 1);
    add(new OpenWindowButton(new Window("Create Height",
        new CreateHeightPane(heights, minHeightObs, maxHeightObs), 300, 450), "Create Height..."),
        1, 1);
    add(spreadRateFilePicker, 0, 2);
    add(new OpenWindowButton(
        new Window("Create Spread Rate", new CreateSpreadRatePane(spreadRates), 300, 450),
        "Create Spread Rate..."), 1, 2);
    add(simulateButton, 0, 3);
  }
}
