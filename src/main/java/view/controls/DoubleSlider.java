package view.controls;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import util.ThreadUtils;
import util.observable.Observable;

public class DoubleSlider extends GridPane {

  public DoubleSlider(Observable<Double> value, String labelValue, double min, double max) {
    setPadding(new Insets(5, 5, 5, 5));
    Label label = new Label(labelValue);
    GridPane.setMargin(label, new Insets(0, 10, 0, 0));
    Label valueLabel = new Label(String.format("%.2f", value.get()));

    Slider slider = new Slider(min, max, value.get());
    slider.valueProperty().addListener((obs, oldValue, newValue) -> {
      valueLabel.setText(String.format("%.2f", newValue));
      ThreadUtils.submit(() -> {
        value.update(1 / newValue.doubleValue());
      });
    });
    GridPane.setMargin(slider, new Insets(0, 10, 0, 0));


    add(label, 0, 0);
    add(slider, 1, 0);
    add(valueLabel, 2, 0);
  }
}
