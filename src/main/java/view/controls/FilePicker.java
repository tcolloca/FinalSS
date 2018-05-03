package view.controls;

import java.io.File;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import util.observable.Observable;

public class FilePicker extends GridPane {

  public FilePicker(Observable<File> value, String buttonLabel, double buttonMinWidth) {
    setPadding(new Insets(5, 5, 5, 5));
    TextField fileTextField = new TextField();
    fileTextField.setPrefWidth(150);
    GridPane.setMargin(fileTextField, new Insets(0, 5, 0, 0));
    
    FileChooser fileChooser = new FileChooser();
    Button loadButton = new Button(buttonLabel);
    loadButton.setMinWidth(buttonMinWidth);
    
    loadButton.setOnAction((final ActionEvent e) -> {
      File file = fileChooser.showOpenDialog(null);
      if (file != null) {
        fileTextField.setText(file.getName());
        value.update(file);
      }
    });
    
    add(fileTextField, 0, 0);
    add(loadButton, 1, 0);
  }
}
