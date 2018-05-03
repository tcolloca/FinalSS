package view.controls;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import util.observable.Observable;

public class TextPane extends GridPane {

  public TextPane(Observable<?> value, String labelValue, String format) {
    setPadding(new Insets(5, 5, 5, 5));
    Label label = new Label(labelValue);
    GridPane.setMargin(label, new Insets(0, 5, 0, 0));
    Label content = new Label();
    value.addListener((oldVal, newVal) -> Platform
        .runLater(() -> content.setText(String.format(format, value.get()))));
    add(label, 0, 0);
    add(content, 1, 0);
  }
}
