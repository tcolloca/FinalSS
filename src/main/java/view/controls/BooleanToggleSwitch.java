package view.controls;

import org.controlsfx.control.ToggleSwitch;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import util.ThreadUtils;
import util.observable.Observable;

public class BooleanToggleSwitch extends GridPane {

  public BooleanToggleSwitch(Observable<Boolean> value, String labelValue) {
    setPadding(new Insets(5, 5, 5, 5));
    Label label = new Label(labelValue);
    ToggleSwitch toggleSwitch = new ToggleSwitch();
    toggleSwitch.setSelected(value.get());
    toggleSwitch.selectedProperty()
        .addListener((observable, oldValue, newValue) -> ThreadUtils.submit(() -> {
          value.update(newValue);
        }));
    add(label, 0, 0);
    add(toggleSwitch, 1, 0);
  }
}
