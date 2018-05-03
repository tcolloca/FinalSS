package view.controls;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import util.ThreadUtils;
import util.observable.Observable;

public class ComboBoxPane extends GridPane {

  public ComboBoxPane(Observable<String> value, String labelValue, List<String> options) {
    setPadding(new Insets(5, 5, 5, 5));
    Label label = new Label(labelValue);
    GridPane.setMargin(label, new Insets(0, 5, 0, 0));
    ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableList(options));
    comboBox.setPrefWidth(200);
    comboBox.valueProperty().set(value.get());
    comboBox.valueProperty().addListener((obs, oldValue, newValue) -> ThreadUtils.submit(() -> {
        value.update(newValue);
    }));
    add(label, 0, 0);
    add(comboBox, 1, 0);
  }
}
