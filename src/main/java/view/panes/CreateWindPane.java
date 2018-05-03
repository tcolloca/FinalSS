package view.panes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import model.Direction;
import util.DataGenerator;
import util.FileUtils;
import util.observable.Observable;
import view.controls.ComboBoxPane;
import view.controls.FileSaver;
import view.controls.InputPane;

public class CreateWindPane extends GridPane {

  private static final List<String> OPTIONS =
      Arrays.asList("Constant", "Constant Linear Front", "Random", "Random Constant Linear Front", 
          "Example");

  private final Observable<String> widthObs;
  private final Observable<String> heightObs;
  private final Observable<String> typeObs;
  private final Observable<String> speedObs;
  private final Observable<String> minSpeedObs;
  private final Observable<String> maxSpeedObs;
  private final Observable<String> directionObs;

  private final InputPane widthPane;
  private final InputPane heightPane;
  private final ComboBoxPane typePane;
  private final InputPane speedPane;
  private final InputPane minSpeedPane;
  private final InputPane maxSpeedPane;
  private final ComboBoxPane directionPane;

  public CreateWindPane(Observable<double[][][]> winds) {
    this.typeObs = new Observable<>(OPTIONS.get(0));
    this.widthObs = new Observable<>(String.valueOf(101));
    this.heightObs = new Observable<>(String.valueOf(101));
    this.speedObs = new Observable<>(String.valueOf(1.0));
    this.minSpeedObs = new Observable<>(String.valueOf(1.0));
    this.maxSpeedObs = new Observable<>(String.valueOf(10.0));
    this.directionObs = new Observable<>(Direction.N.name());

    this.widthPane = new InputPane(widthObs, "Width: ");
    this.heightPane = new InputPane(heightObs, "Height: ");
    this.typePane = new ComboBoxPane(typeObs, "Type: ", OPTIONS);
    this.speedPane = new InputPane(speedObs, "Speed: ");
    this.minSpeedPane = new InputPane(minSpeedObs, "Min Speed: ");
    this.maxSpeedPane = new InputPane(maxSpeedObs, "Max Speed: ");
    this.directionPane = new ComboBoxPane(directionObs, "Direction: ",
        Arrays.stream(Direction.values()).map(e -> e.name()).collect(Collectors.toList()));

    setGuiChangeByType();
    
    add(widthPane, 0, 0);
    add(heightPane, 0, 1);
    add(typePane, 0, 2);
    add(speedPane, 0, 3);
    add(minSpeedPane, 0, 4);
    add(maxSpeedPane, 0, 5);
    add(directionPane, 0, 6);
    HBox aux = new HBox();
    aux.getChildren().add(createButton(winds));
    Function<String, Void> saveFunction = (path) -> { 
      try {
        FileUtils.saveWinds(path, winds.get());
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    };
    aux.getChildren().add(new FileSaver(saveFunction, "Save Winds...", 100.0));
    GridPane.setMargin(aux, new Insets(0, 0, 0, 10));
    add(aux, 0, 7);
  }
  
  private Button createButton(Observable<double[][][]> winds) {
    Button createButton = new Button("Create");
    createButton.setPrefWidth(100);
    createButton.setOnAction((event) -> {
      int width = Integer.parseInt(widthObs.get());
      int height = Integer.parseInt(heightObs.get());
      double speed = Double.parseDouble(speedObs.get());
      double minSpeed = Double.parseDouble(minSpeedObs.get());
      double maxSpeed = Double.parseDouble(maxSpeedObs.get());
      Direction direction = Direction.parseDirection(directionObs.get());
      switch (typeObs.get()) {
        case "Constant":
          winds.update(DataGenerator.constantWind(width, height, speed));
          break;
        case "Constant Linear Front":
          winds
              .update(DataGenerator.linearWindFront(width, height, direction, (row, col) -> speed));
          break;
        case "Random":
          winds.update(DataGenerator.randomWind(width, height, minSpeed, maxSpeed));
          break;
        case "Random Constant Linear Front":
          winds.update(
              DataGenerator.randomLinearWindFront(width, height, direction, minSpeed, maxSpeed));
          break;
        case "Example":
          winds.update(DataGenerator.exampleFunctionWind(width, height));
      }
    }); 
    return createButton;
  }
  
  private void setGuiChangeByType() {
    typeObs.addListener((oldValue, newValue) -> {
      switch (newValue) {
        case "Constant":
          speedPane.setDisable(false);
          minSpeedPane.setDisable(true);
          maxSpeedPane.setDisable(true);
          directionPane.setDisable(true);
          break;
        case "Constant Linear Front":
          speedPane.setDisable(false);
          minSpeedPane.setDisable(true);
          maxSpeedPane.setDisable(true);
          directionPane.setDisable(false);
          break;
        case "Random":
          speedPane.setDisable(true);
          minSpeedPane.setDisable(false);
          maxSpeedPane.setDisable(false);
          directionPane.setDisable(true);
          break;
        case "Random Constant Linear Front":
          speedPane.setDisable(true);
          minSpeedPane.setDisable(false);
          maxSpeedPane.setDisable(false);
          directionPane.setDisable(false);
          break;
        case "Example":
          speedPane.setDisable(true);
          minSpeedPane.setDisable(true);
          maxSpeedPane.setDisable(true);
          directionPane.setDisable(true);
          break;
      }
    });
    
    typeObs.update("Constant");
  }
}
