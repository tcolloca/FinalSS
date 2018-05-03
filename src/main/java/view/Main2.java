package view;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.Function;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import model.ForestCellularAutomaton1;
import util.DataGenerator;
import util.ThreadUtils;
import util.observable.Observable;
import view.controls.Window;
import view.panes.SimulationPane;
import view.panes.StartOptionsPane;

public class Main2 extends Application {

  private static int W = 101;
  private static int H = 101;
  private static double REAL_L = 1;

  private SimulationPane simulationPane;
  private ForestCellularAutomaton1 forest;

  private Observable<double[][][]> windsObs =
      new Observable<double[][][]>(DataGenerator.constantWind(W, H, 1));
  private Observable<double[][]> heightsObs = new Observable<>(DataGenerator.constantHeight(W, H, 1));
  private Observable<double[][]> spreadRatesObs =
      new Observable<>(DataGenerator.constantFireSpreadRate(W, H, 1));

  private final Observable<Double> minHeightObs = new Observable<>(1.0);
  private final Observable<Double> maxHeightObs = new Observable<>(10000.0);

  private final Observable<Boolean> simulate = new Observable<>(false);

  public static void main(String[] args) throws IOException {
    Application.launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    setStartWindow(stage);

    ThreadUtils.submit(() -> {
      while (true) {
        try {
          while (!simulate.get()) {
            System.out.println("Awaiting...");
            simulate.await();
          }
          simulate.update(false);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        simulate();
      }
    });
  }

  public void simulate() {
    System.out.println("Create forest");
    createForest();
    System.out.println("Forest created");
    FutureTask<Void> task = new FutureTask<Void>(() -> setSimulationWindow(), null);
    Platform.runLater(task);
    try {
      task.get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    simulationPane.simulate();
  }

  private void setStartWindow(Stage stage) {
    Window window =
        new Window("Forest Fire Spreading Simulation Config", new StartOptionsPane(windsObs,
            heightsObs, minHeightObs, maxHeightObs, spreadRatesObs, simulate), 500, 300);

    window.setOnCloseRequest((event) -> {
      Platform.exit();
      System.exit(0);
    });

    stage.hide();
    window.show();
  }

  private void setSimulationWindow() {
    System.out.println("Simulation Window");
    this.simulationPane = new SimulationPane(forest, minHeightObs.get(), maxHeightObs.get());
    System.out.println("sim pane created");
    Window window = new Window("Forest Fire Spreading Simulation", simulationPane,
        simulationPane.getPixelWidth(), simulationPane.getPixelHeight());
    
    window.setOnCloseRequest((event) -> {
      simulationPane.stop();
    });
    
    window.show();
  }

  private void createForest() {
    H = windsObs.get().length;
    W = windsObs.get()[0].length;
    double[][] ignitions = new double[H][W];
    double[][] spreadRates = spreadRatesObs.get();

    ignitions[(H - 1) / 2][(W - 1) / 2] = 1;
    spreadRates[(H - 1) / 2][(W - 1) / 2] = 1;

    Function<Double, Double> phi = x -> {
      if (x > 0) {
        return 1 + Math.sqrt(x) / 10;
      } else {
        return Math.exp(x);
      }
    };

    System.out.println("frestss");
    forest = new ForestCellularAutomaton1(spreadRates, windsObs.get(), heightsObs.get(),
        ignitions, phi, REAL_L);
    System.out.println("forest done.");
  }
}
