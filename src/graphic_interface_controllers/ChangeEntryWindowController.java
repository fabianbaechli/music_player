package graphic_interface_controllers;

import com.jfoenix.controls.JFXListView;
import javafx.fxml.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChangeEntryWindowController implements Initializable {
    @FXML
    JFXListView<BorderPane> listView;

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        try {
            StackPane pane = FXMLLoader.load(ChangeEntryWindowController.class.getResource("/graphic_interface/entry_representation.fxml"));
            BorderPane borderPane = (BorderPane) pane.getChildren().get(0);
            listView.getItems().add(borderPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
