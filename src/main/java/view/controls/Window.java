package view.controls;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Window extends Stage {

  public Window(String title, Parent content, double width, double height) {
    setTitle(title);
    setScene(new Scene(content, width, height));
  }
}
