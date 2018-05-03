package view.panes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import model.Direction;
import util.DataGenerator;
import util.FileUtils;
import util.observable.Observable;
import view.controls.ComboBoxPane;
import view.controls.FileSaver;
import view.controls.InputPane;

public class CreateSpreadRatePane extends GridPane {

  private static final List<String> OPTIONS =
      Arrays.asList("Constant", "Linear", "Random", "Random Perlin");

  private final Observable<String> widthObs;
  private final Observable<String> heightObs;
  private final Observable<String> typeObs;
  private final Observable<String> spreadRateObs;
  private final Observable<String> minSpreadRateObs;
  private final Observable<String> maxSpreadRateObs;
  private final Observable<String> directionObs;
  private final Observable<String> octavesObs;
  private final Observable<String> lacunarityObs;
  private final Observable<String> persistanceObs;


  private final InputPane widthPane;
  private final InputPane heightPane;
  private final ComboBoxPane typePane;
  private final InputPane spreadRatePane;
  private final InputPane minSpreadRatePane;
  private final InputPane maxSpreadRatePane;
  private final ComboBoxPane directionPane;
  private final InputPane octavesPane;
  private final InputPane lacunarityPane;
  private final InputPane persistancePane;

  public CreateSpreadRatePane(Observable<double[][]> spreadRates) {
    this.typeObs = new Observable<>(OPTIONS.get(0));
    this.widthObs = new Observable<>(String.valueOf(101));
    this.heightObs = new Observable<>(String.valueOf(101));
    this.spreadRateObs = new Observable<>(String.valueOf(1.0));
    this.minSpreadRateObs = new Observable<>(String.valueOf(1));
    this.maxSpreadRateObs = new Observable<>(String.valueOf(3));
    this.directionObs = new Observable<>(Direction.N.name());
    this.octavesObs = new Observable<>(String.valueOf(2));
    this.lacunarityObs = new Observable<>(String.valueOf(2.0));
    this.persistanceObs = new Observable<>(String.valueOf(2.0));

    this.widthPane = new InputPane(widthObs, "Width: ");
    this.heightPane = new InputPane(heightObs, "Height: ");
    this.typePane = new ComboBoxPane(typeObs, "Type: ", OPTIONS);
    this.spreadRatePane = new InputPane(spreadRateObs, "Spread Rate: ");
    this.minSpreadRatePane = new InputPane(minSpreadRateObs, "Min Spread Rate: ");
    this.maxSpreadRatePane = new InputPane(maxSpreadRateObs, "Max Spread Rate: ");
    this.directionPane = new ComboBoxPane(directionObs, "Direction: ",
        Arrays.stream(Direction.values()).map(e -> e.name()).collect(Collectors.toList()));
    this.octavesPane = new InputPane(octavesObs, "Octaves: ");
    this.lacunarityPane = new InputPane(lacunarityObs, "Lacunarity: ");
    this.persistancePane = new InputPane(persistanceObs, "Persistance: ");

    setGuiChangeByType();

    add(widthPane, 0, 0);
    add(heightPane, 0, 1);
    add(typePane, 0, 2);
    add(spreadRatePane, 0, 3);
    add(minSpreadRatePane, 0, 4);
    add(maxSpreadRatePane, 0, 5);
    add(directionPane, 0, 6);
    add(octavesPane, 0, 7);
    add(lacunarityPane, 0, 8);
    add(persistancePane, 0, 9);
    GridPane aux = new GridPane();
    aux.add(createButton(spreadRates), 0, 0);
    Function<String, Void> saveFunction = (path) -> { 
      try {
        FileUtils.saveSpreadRates(path, spreadRates.get());
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    };
    GridPane.setMargin(aux, new Insets(0, 0, 0, 10));
    aux.add(new FileSaver(saveFunction, "Save Spread Rates...", 130.0), 1, 0);
    add(aux, 0, 10);
  }

  private Button createButton(Observable<double[][]> spreadRates) {
    Button createButton = new Button("Create");
    createButton.setPrefWidth(130);
    createButton.setOnAction((event) -> {
      int width = Integer.parseInt(widthObs.get());
      int height = Integer.parseInt(heightObs.get());
      double terrainSpreadRate = Double.parseDouble(spreadRateObs.get());
      double minSpreadRate = Double.parseDouble(minSpreadRateObs.get());
      double maxSpreadRate = Double.parseDouble(maxSpreadRateObs.get());
      int octaves = Integer.parseInt(octavesObs.get());
      double lacunarity = Double.parseDouble(lacunarityObs.get());
      double persistance = Double.parseDouble(persistanceObs.get());
      Direction direction = Direction.parseDirection(directionObs.get());
      switch (typeObs.get()) {
        case "Constant":
          spreadRates
              .update(DataGenerator.constantFireSpreadRate(width, height, terrainSpreadRate));
          break;
        case "Linear":
          spreadRates.update(DataGenerator.linearFireSpreadRate(width, height, direction));
          break;
        case "Random":
          spreadRates.update(
              DataGenerator.randomFireSpreadRate(width, height, minSpreadRate, maxSpreadRate));
          break;
        case "Random Perlin":
          spreadRates.update(DataGenerator.randomFirePerlinSpreadRate(width, height, octaves,
              lacunarity, persistance, minSpreadRate, maxSpreadRate));
          break;
      }
    });
    return createButton;
  }

  private void setGuiChangeByType() {
    typeObs.addListener((oldValue, newValue) -> {
      switch (newValue) {
        case "Constant":
          spreadRatePane.setDisable(false);
          minSpreadRatePane.setDisable(true);
          maxSpreadRatePane.setDisable(true);
          directionPane.setDisable(true);
          octavesPane.setDisable(true);
          lacunarityPane.setDisable(true);
          persistancePane.setDisable(true);
          break;
        case "Linear":
          spreadRatePane.setDisable(true);
          minSpreadRatePane.setDisable(true);
          maxSpreadRatePane.setDisable(true);
          directionPane.setDisable(false);
          octavesPane.setDisable(true);
          lacunarityPane.setDisable(true);
          persistancePane.setDisable(true);
          break;
        case "Random":
          spreadRatePane.setDisable(true);
          minSpreadRatePane.setDisable(false);
          maxSpreadRatePane.setDisable(false);
          directionPane.setDisable(true);
          octavesPane.setDisable(true);
          lacunarityPane.setDisable(true);
          persistancePane.setDisable(true);
          break;
        case "Random Perlin":
          spreadRatePane.setDisable(true);
          minSpreadRatePane.setDisable(false);
          maxSpreadRatePane.setDisable(false);
          directionPane.setDisable(true);
          octavesPane.setDisable(false);
          lacunarityPane.setDisable(false);
          persistancePane.setDisable(false);
          break;
      }
    });

    typeObs.update("Constant");
  }
}
