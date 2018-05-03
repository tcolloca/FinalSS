package view.controls;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import util.ThreadUtils;
import util.observable.Observable;

public class IntegerSpinnerPane extends GridPane {

  public IntegerSpinnerPane(Observable<Integer> value, String labelValue, int min, int max) {
    setPadding(new Insets(5, 5, 5, 5));
    Label label = new Label(labelValue);
    GridPane.setMargin(label, new Insets(0, 10, 0, 0));
    Spinner<Integer> spinner = new Spinner<>(min, max, value.get());
    spinner.valueProperty().addListener((obs, oldValue, newValue) -> ThreadUtils.submit(() -> {
        value.update(newValue);
    }));
    spinner.setEditable(true);
    spinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        spinner.increment(0); // won't change value, but will commit editor
      }
    });

    add(label, 0, 0);
    add(spinner, 1, 0);
  }
}
