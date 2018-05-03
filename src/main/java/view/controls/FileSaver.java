package view.controls;

import java.io.File;
import java.util.function.Function;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

public class FileSaver extends GridPane {

  public FileSaver(Function<String, Void> saveFunction,
      String buttonLabel, double buttonMinWidth) {
    setPadding(new Insets(0, 0, 0, 10));

    FileChooser fileChooser = new FileChooser();
    Button saveButton = new Button(buttonLabel);
    saveButton.setMinWidth(buttonMinWidth);

    saveButton.setOnAction((final ActionEvent e) -> {
      FileChooser.ExtensionFilter extFilter =
          new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
      fileChooser.getExtensionFilters().add(extFilter);
      File file = fileChooser.showSaveDialog(null);
      if (file != null) {
        saveFunction.apply(file.getAbsolutePath());
      }
    });

    add(saveButton, 0, 0);
  }
}
