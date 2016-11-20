package view;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import model.Cell;
import model.Direction;
import model.ForestCellularAutomaton;

public class Main extends Application {
	
	private static final int SCREEN_W = 600;
	private static final int SCREEN_H = 600;
	private static final Color BURN_COLOR = Color.valueOf("#030");
	
	private static final int W = 100;
	private static final int H = 100;
	private static final double SIZE_ADJUSTMENT = 0.5;
	
	private StackPane[][] matrix = new StackPane[H][W]; 
	private GridPane gridPane;
	private ForestCellularAutomaton forest;
	private AtomicBoolean newState;

	public static void main(String[] args) throws IOException {
	    Application.launch(args);
	  }
	  
	  @Override
	  public void start(Stage stage) throws Exception {
	    stage.setTitle("Forest Fire Spreading Simulation");

	    initMainGridPane(W, H);

	    Scene scene = new Scene(gridPane, SCREEN_W, SCREEN_H, Color.WHITE);
	    stage.setScene(scene);
	    stage.show();
	    
	    
	    forest = new ForestCellularAutomaton(W, H);
//	    int[][] points = new int[][]{
//	    		{50, 50},
//	    		{49, 50},
//	    		{50, 49},
//	    		{50, 51},
//	    		{51, 50}
//	    };
//	    for (int[] point : points) {
//	    	burnAll(point[0], point[1]);
//	    	forest.setCell(point[0], point[1], 1);
//	    }
	    
	    int r = 3;
	    int c = 50;
	    for (int i =  c - 5; i <  c + 5; i++) {
	    	for (int j = c -5; j < c + 5; j++) {
	    		if (Math.sqrt((c - i)*(c - i) + (c - j)*(c - j)) < r*r) {
	    			burnAll(i, j);
	    	    	forest.setCell(i, j, 1);
	    		}
	    	}
	    }
	    
	    newState = new AtomicBoolean(false);

	    new Thread(() -> {
		    int t = 0;
		    while (t++ < 1000) {
		    	while (newState.get()) {
		    		Thread.yield();
		    	}
		    	forest.next();
		    	newState.set(true);
		    }
	    }).start();
	    
	    
	    Task<Void> task = new Task<Void>() {
	    	  @Override
	    	  public Void call() throws Exception {
	    	    while (true) {
	    	      Platform.runLater(new Runnable() {
	    	        @Override
	    	        public void run() {
	    	          draw();
	    	        }
	    	      });
	    	      Thread.sleep(1000);
	    	    }
	    	  }
	    	};
	    	Thread th = new Thread(task);
	    	th.setDaemon(true);
	    	th.start();
	  }
	  
	  private void draw() {
		  for (int h = 0; h < H; h++) {
  			for (int w = 0; w < W; w++) {
  				Cell cell = forest.getCell(h, w);
  				if (cell.getValue() == 1) {
  					burnAll(h, w);
  				} else if (cell.getValue() > 0) {
  					burnPartially(h, w, cell.getDirection());
  				}
  			}
  		}
  		newState.set(false);
	  }

	  private GridPane initMainGridPane(int W, int H) {
	    gridPane = new GridPane();
        for (int row = 0; row < W; row++) {
            for (int col = 0; col < H; col ++) {
                StackPane square = new StackPane();
                square.setAlignment(Pos.TOP_LEFT);
                square.setStyle("-fx-border-color: black");
                gridPane.add(square, col, row);
                matrix[row][col] = square;
                paintSquare(row, col, Color.GREEN);
            }
        }
        for (int i = 0; i < W; i++) {
            gridPane.getColumnConstraints().add(new ColumnConstraints(5, Control.USE_COMPUTED_SIZE, Double.POSITIVE_INFINITY, Priority.ALWAYS, HPos.CENTER, true));
        }
        for (int i = 0; i < H; i++) {
            gridPane.getRowConstraints().add(new RowConstraints(5, Control.USE_COMPUTED_SIZE, Double.POSITIVE_INFINITY, Priority.ALWAYS, VPos.CENTER, true));
        }
	    return gridPane;
	  }
	  
	  public void burnAll(int row, int col) {
          paintSquare(row, col, BURN_COLOR);
	  }
	  
	  public void paintSquare(int row, int col, Color color) {
          double width = SCREEN_W / W;
          double height = SCREEN_H / H;
          Polygon polyBurn = new Polygon();
          polyBurn.getPoints().addAll(new Double[]{
                  0.0, 0.0,
                  width, 0.0,
                  width, height,
                  0.0, height,
                  });
          polyBurn.setFill(color);

          StackPane square = matrix[row][col];
          square.getChildren().add(polyBurn);
	  }
	  
	  public void burnPartially(int row, int col, Direction direction) {
		  double angle = 0;
		  switch (direction) {
		  case NW:
			  angle = 180;
			  break;
		  case NE:
			  angle = 270;
			  break;
		  case SE:
			  angle = 0;
			  break;
		  case SW:
			  angle = 90;
			  break;
		  }
          double width = SCREEN_W / W;
          double height = SCREEN_H / H;
          addPolygon(row, col, new Double[]{
                  0.0, 0.0,
                  width, 0.0,
                  width, height/2 * SIZE_ADJUSTMENT,
                  width/2 * SIZE_ADJUSTMENT, height,
                  0.0, height,
                  }, angle, BURN_COLOR);
	  }
	  
	  private void addPolygon(int row, int col, Double[] points, double angle, Color color) {
		  Polygon poly = new Polygon();
          poly.getPoints().addAll(points);
          poly.setFill(color);
          poly.setRotate(angle);

          StackPane square = matrix[row][col];
          square.getChildren().add(poly);
	  }
}
