package view.controls;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class OpenWindowButton extends Button {

  public OpenWindowButton(Stage stage, String label) {
    super(label);
    setOnAction((final ActionEvent e) -> {
      stage.show();
    });
  }
}
