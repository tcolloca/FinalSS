package view.controls;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import util.observable.Observable;

public class InputPane extends GridPane {

  public InputPane(Observable<String> value, String labelValue) {
    setPadding(new Insets(5, 5, 5, 5));
    Label label = new Label(labelValue);
    GridPane.setMargin(label, new Insets(0, 5, 0, 0));
    TextField fileTextField = new TextField();
    fileTextField.setPrefWidth(80);
    fileTextField.setText(String.valueOf(value.get()));
    fileTextField.textProperty().addListener((obs, oldValue, newValue) -> {
      value.update(newValue);
    });
    add(label, 0, 0);
    add(fileTextField, 1, 0);
  }
}
