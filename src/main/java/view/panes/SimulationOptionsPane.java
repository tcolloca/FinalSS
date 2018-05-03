package view.panes;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import util.observable.Observable;
import util.observable.ObservableList;
import view.controls.BooleanToggleSwitch;
import view.controls.DoubleSlider;
import view.controls.IntegerSpinnerPane;
import view.controls.NumberChartPane;
import view.controls.OpenWindowButton;
import view.controls.TextPane;
import view.controls.Window;

public class SimulationOptionsPane extends GridPane {

  private static final double WIDTH = 300;

  public SimulationOptionsPane(Observable<Integer> iteration, Observable<Double> burntArea,
      ObservableList<Number[]> burntAreaSeries, Observable<Integer> maxIterations,
      Observable<Double> speed, Observable<Boolean> paintTerrain, Observable<Boolean> play) {
    setPrefWidth(WIDTH);
    IntegerSpinnerPane maxIterationsSpinner =
        new IntegerSpinnerPane(maxIterations, "Max Iterations:", 1, 1000);
    DoubleSlider speedSlider = new DoubleSlider(speed, "Speed(1/s): ", 0.1, 10);
    BooleanToggleSwitch paintTerrainSwitch =
        new BooleanToggleSwitch(paintTerrain, "Paint Terrain: ");
    BooleanToggleSwitch playSwitch = new BooleanToggleSwitch(play, "Play:");
    TextPane iterationText = new TextPane(iteration, "Iteration:", "%d");
    TextPane burntAreaText = new TextPane(burntArea, "Burnt Area:", "%.1f");
    NumberChartPane chartPane = new NumberChartPane(burntAreaSeries, "Iteration", "Burnt Area");
    Button openChartButton = new OpenWindowButton(new Window("Burnt Area Chart", chartPane, 500, 300),
        "Open Burnt Area Chart...");
    GridPane.setMargin(openChartButton, new Insets(0, 0, 0, 70));

    add(maxIterationsSpinner, 0, 3);
    add(speedSlider, 0, 4);
    add(paintTerrainSwitch, 0, 5);
    add(playSwitch, 0, 6);
    add(iterationText, 0, 7);
    add(burntAreaText, 0, 8);
    add(openChartButton, 0, 9);
  }

  public double getPixelWidth() {
    return WIDTH;
  }
}
