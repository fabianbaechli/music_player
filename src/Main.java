import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import Music.MusicFile;
import Music.MusicFolder;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Main extends Application implements ObserverPattern.Observer {
    private static final String os = System.getProperty("os.name");
    private static StackPane page;
    private List<MusicFolder> musicFolder = new ArrayList<>();

    public static void main(String[] args) {
        Application.launch(Main.class, (java.lang.String[]) null);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            MenuBar menuBar = new MenuBar();
            final Menu fileMenu = new Menu("File");
            final Menu optionsMenu = new Menu("Options");
            final Menu helpMenu = new Menu("Help");

            MenuItem addFolderContent = new MenuItem();
            addFolderContent.setText("Add Folder Content");
            addFolderContent.setOnAction((ActionEvent event) -> {

                // Displays a dialog, in which a folder can be chosen
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File folder = directoryChooser.showDialog(primaryStage);
                if (folder != null) {
                    Controller controller = new Controller();
                    controller.addObserver(this);
                    musicFolder.add(controller.handleFolder(folder));
                }
            });
            fileMenu.getItems().addAll(addFolderContent);
            if (os != null && os.startsWith("Mac"))
                menuBar.useSystemMenuBarProperty().set(true);

            menuBar.getMenus().addAll(fileMenu, optionsMenu, helpMenu);
            BorderPane borderPane = new BorderPane();
            borderPane.setTop(menuBar);

            page = FXMLLoader.load(Main.class.getResource("/graphic_interface/mainWindow.fxml"));
            page.getChildren().add(borderPane);
            Scene scene = new Scene(page);

            primaryStage.setScene(scene);
            primaryStage.setTitle("Login");
            primaryStage.show();
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void update(String name, double songLength) {
        for (MusicFolder aMusicFolder : musicFolder) {
            for (MusicFile aMusicFile : aMusicFolder.getFiles()) {
                if (aMusicFile.getName().equals(name)) {
                    aMusicFile.setDuration(songLength);
                }
            }
        }
    }
}