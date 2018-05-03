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

public class CreateHeightPane extends GridPane {

  private static final List<String> OPTIONS =
      Arrays.asList("Constant", "Linear", "Random", "Random Perlin");

  private final Observable<String> widthObs;
  private final Observable<String> heightObs;
  private final Observable<String> typeObs;
  private final Observable<String> terrainHeightObs;
  private final Observable<String> minHeightObs;
  private final Observable<String> maxHeightObs;
  private final Observable<String> directionObs;
  private final Observable<String> octavesObs;
  private final Observable<String> lacunarityObs;
  private final Observable<String> persistanceObs;


  private final InputPane widthPane;
  private final InputPane heightPane;
  private final ComboBoxPane typePane;
  private final InputPane terrainHeightPane;
  private final InputPane minHeightPane;
  private final InputPane maxHeightPane;
  private final ComboBoxPane directionPane;
  private final InputPane octavesPane;
  private final InputPane lacunarityPane;
  private final InputPane persistancePane;

  public CreateHeightPane(Observable<double[][]> heights, Observable<Double> minHeightDoubleObs,
      Observable<Double> maxHeightDoubleObs) {
    this.typeObs = new Observable<>(OPTIONS.get(0));
    this.widthObs = new Observable<>(String.valueOf(101));
    this.heightObs = new Observable<>(String.valueOf(101));
    this.terrainHeightObs = new Observable<>(String.valueOf(1.0));
    this.minHeightObs = new Observable<>(String.valueOf(minHeightDoubleObs.get()));
    minHeightObs.addListener((oldVal, newVal) -> minHeightDoubleObs.update(Double.valueOf(newVal)));
    this.maxHeightObs = new Observable<>(String.valueOf(maxHeightDoubleObs.get()));
    maxHeightObs.addListener((oldVal, newVal) -> maxHeightDoubleObs.update(Double.valueOf(newVal)));
    this.directionObs = new Observable<>(Direction.N.name());
    this.octavesObs = new Observable<>(String.valueOf(2));
    this.lacunarityObs = new Observable<>(String.valueOf(2.0));
    this.persistanceObs = new Observable<>(String.valueOf(2.0));

    this.widthPane = new InputPane(widthObs, "Width: ");
    this.heightPane = new InputPane(heightObs, "Height: ");
    this.typePane = new ComboBoxPane(typeObs, "Type: ", OPTIONS);
    this.terrainHeightPane = new InputPane(terrainHeightObs, "Terrain Height: ");
    this.minHeightPane = new InputPane(minHeightObs, "Min Height: ");
    this.maxHeightPane = new InputPane(maxHeightObs, "Max Height: ");
    this.directionPane = new ComboBoxPane(directionObs, "Direction: ",
        Arrays.stream(Direction.values()).map(e -> e.name()).collect(Collectors.toList()));
    this.octavesPane = new InputPane(octavesObs, "Octaves: ");
    this.lacunarityPane = new InputPane(lacunarityObs, "Lacunarity: ");
    this.persistancePane = new InputPane(persistanceObs, "Persistance: ");

    setGuiChangeByType();

    add(widthPane, 0, 0);
    add(heightPane, 0, 1);
    add(typePane, 0, 2);
    add(terrainHeightPane, 0, 3);
    add(minHeightPane, 0, 4);
    add(maxHeightPane, 0, 5);
    add(directionPane, 0, 6);
    add(octavesPane, 0, 7);
    add(lacunarityPane, 0, 8);
    add(persistancePane, 0, 9);
    GridPane aux = new GridPane();
    aux.add(createButton(heights), 0, 0);
    Function<String, Void> saveFunction = (path) -> { 
      try {
        FileUtils.saveHeights(path, heights.get(), minHeightDoubleObs.get(), maxHeightDoubleObs.get());
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    };
    GridPane.setMargin(aux, new Insets(0, 0, 0, 10));
    aux.add(new FileSaver(saveFunction, "Save Heights...", 100.0), 1, 0);
    add(aux, 0, 10);
  }

  private Button createButton(Observable<double[][]> heights) {
    Button createButton = new Button("Create");
    createButton.setPrefWidth(100);
    createButton.setOnAction((event) -> {
      int width = Integer.parseInt(widthObs.get());
      int height = Integer.parseInt(heightObs.get());
      double terrainHeight = Double.parseDouble(terrainHeightObs.get());
      double minHeight = Double.parseDouble(minHeightObs.get());
      double maxHeight = Double.parseDouble(maxHeightObs.get());
      int octaves = Integer.parseInt(octavesObs.get());
      double lacunarity = Double.parseDouble(lacunarityObs.get());
      double persistance = Double.parseDouble(persistanceObs.get());
      Direction direction = Direction.parseDirection(directionObs.get());
      switch (typeObs.get()) {
        case "Constant":
          heights.update(DataGenerator.constantHeight(width, height, terrainHeight));
          break;
        case "Linear":
          minHeightObs.update(String.valueOf((- width - height) * terrainHeight));
          maxHeightObs.update(String.valueOf((width + height) * terrainHeight));
          heights.update(DataGenerator.linearHeight(width, height, direction, terrainHeight));
          break;
        case "Random":
          heights.update(DataGenerator.randomHeight(width, height, minHeight, maxHeight));
          break;
        case "Random Perlin":
          heights.update(DataGenerator.randomPerlinHeight(width, height, octaves, lacunarity,
              persistance, minHeight, maxHeight));
          break;
      }
    });
    return createButton;
  }

  private void setGuiChangeByType() {
    typeObs.addListener((oldValue, newValue) -> {
      switch (newValue) {
        case "Constant":
          terrainHeightPane.setDisable(false);
          minHeightPane.setDisable(true);
          maxHeightPane.setDisable(true);
          directionPane.setDisable(true);
          octavesPane.setDisable(true);
          lacunarityPane.setDisable(true);
          persistancePane.setDisable(true);
          break;
        case "Linear":
          terrainHeightPane.setDisable(false);
          minHeightPane.setDisable(true);
          maxHeightPane.setDisable(true);
          directionPane.setDisable(false);
          octavesPane.setDisable(true);
          lacunarityPane.setDisable(true);
          persistancePane.setDisable(true);
          break;
        case "Random":
          terrainHeightPane.setDisable(true);
          minHeightPane.setDisable(false);
          maxHeightPane.setDisable(false);
          directionPane.setDisable(true);
          octavesPane.setDisable(true);
          lacunarityPane.setDisable(true);
          persistancePane.setDisable(true);
          break;
        case "Random Perlin":
          terrainHeightPane.setDisable(true);
          minHeightPane.setDisable(false);
          maxHeightPane.setDisable(false);
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
