package graphic_interface_controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.fxml.*;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import Controller.Controller;

public class ChangeEntryWindowController implements Initializable {
    @FXML
    JFXListView<BorderPane> listView;

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        try {
            Controller controller = new Controller();
            String[] locations = controller.getLocationsFromUserFile();

            for (int i = 0; i < locations.length; i++) {
                if (!locations[i].equals("")) {
                    StackPane pane = FXMLLoader.load(ChangeEntryWindowController.class.getResource("/graphic_interface/entry_representation.fxml"));
                    BorderPane borderPane = (BorderPane) pane.getChildren().get(0);
                    ((Label) borderPane.getLeft()).setText(locations[i]);
                    listView.getItems().add(borderPane);

                    JFXButton button = (JFXButton) borderPane.getRight();
                    button.setOnAction(event -> {
                        listView.getItems().removeAll(borderPane);
                        Main.controller.deleteLineInUserFile(((Label) borderPane.getLeft()).getText());
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
