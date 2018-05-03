package view.panes;

import java.util.concurrent.Semaphore;
import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import model.ForestCellularAutomaton1;
import util.ThreadUtils;
import util.observable.Observable;
import util.observable.ObservableList;
import view.State;

public class SimulationPane extends GridPane {

  private final CellsPane cellsPane;
  private final SimulationOptionsPane optionsPane;

  private final ForestCellularAutomaton1 forest;
  private final Observable<Integer> iteration = new Observable<>(-1);
  private final Observable<Double> burntArea = new Observable<>(0.0);
  private final ObservableList<Number[]> burntAreaSeries = new ObservableList<>();
  private final Observable<Integer> maxIterations = new Observable<>(100);
  private final Observable<Double> speed = new Observable<>(0.5);
  private final Observable<Boolean> paintTerrain = new Observable<>(true);
  private final Observable<Boolean> play = new Observable<>(false);

  private State state = State.NEEDS_DRAW;
  private Semaphore readyForSim =
      ThreadUtils.getSemaphore(ThreadUtils.getLock(state).newCondition(), 0);
  private Semaphore readyForDraw =
      ThreadUtils.getSemaphore(ThreadUtils.getLock(state).newCondition(), 0);
  private Thread simulationThread;
  private Thread drawThread;
  private long time;

  public SimulationPane(ForestCellularAutomaton1 forest, double minHeight, double maxHeight) {
    this.forest = forest;
    this.cellsPane = new CellsPane(forest, paintTerrain, minHeight, maxHeight);
    this.optionsPane = new SimulationOptionsPane(iteration, burntArea, burntAreaSeries,
        maxIterations, speed, paintTerrain, play);
    add(cellsPane, 0, 0);
    add(optionsPane, 1, 0);
  }

  public double getPixelWidth() {
    return cellsPane.getPixelWidth() + optionsPane.getPixelWidth();
  }

  public double getPixelHeight() {
    return cellsPane.getPixelHeight();
  }

  public void simulate() {
    startDrawerThread();
    startStateUpdaterThread();
  }

  private void startStateUpdaterThread() {
    simulationThread = new Thread(() -> {
      try {
        while (!state.equals(State.STOP)) {
          System.out.println("New sim cycle.");
          synchronized (maxIterations) {
            while (iteration.get() >= maxIterations.get()) {
              maxIterations.await();
            }
          }
          iteration.update(iteration.get() + 1);

          // Wait until draw and simulation is being played.
          while (!state.equals(State.STOP)) {
            synchronized (state) {
              if (state.equals(State.NEEDS_SIM) && play.get()) {
                break;
              }
            }
            System.out.println("Doesn't need sim or play is false. Waiting for change...");
            if (!play.get()) {
              play.await();
            } else {
              readyForSim.acquire();
            }
          }
          System.out.println("Get new simulation state...");
          if (forest.next()) {
            synchronized (state) {
              state = state != State.STOP ? State.NEEDS_DRAW : State.STOP;
            }
            System.out.println("Ready to draw :)");
            readyForDraw.release();
          } else {
            break;
          }
        }
        System.out.println("Finished.");
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
    ThreadUtils.submit(simulationThread);
  }

  private void startDrawerThread() {
    drawThread = new Thread(() -> {
      try {
        // Thread.sleep(2000);
        while (!state.equals(State.STOP)) {
          System.out.println("New draw cycle.");
          while (!state.equals(State.STOP)) {
            synchronized (state) {
              if (state.equals(State.NEEDS_DRAW)) {
                break;
              }
            }
            System.out.println("Waiting for draw state...");
            readyForDraw.acquire();
          }
          long elapsedTime = System.currentTimeMillis() - time;
          if (elapsedTime < speed.get() * 1000) {
            Thread.sleep((long) (speed.get() * 1000 - elapsedTime));
          }
          synchronized (state) {
            state = state != State.STOP ? State.DRAWING : State.STOP;
          }
          Platform.runLater(() -> {
            refresh();
            double burntAreaVal = forest.getBurntArea(); 
            burntArea.update(burntAreaVal);
            burntAreaSeries.add(new Number[] {iteration.get(), burntAreaVal});
            time = System.currentTimeMillis();
            synchronized (state) {
              state = state != State.STOP ? State.NEEDS_SIM : State.STOP;
            }
            System.out.println("Ready to sim :)");
            readyForSim.release();
          });
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
    drawThread.setDaemon(true);
    ThreadUtils.submit(drawThread);
  }

  private void refresh() {
    cellsPane.draw();
  }

  public void stop() {
    System.out.println("stop");
    state = State.STOP;
  }
}
