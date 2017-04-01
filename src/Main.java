import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import Music.MusicFile;
import Music.MusicFolder;
import com.sun.jmx.snmp.internal.SnmpSubSystem;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Main extends Application implements ObserverPattern.Observer {
    private int scroll = 0;
    private static final String os = System.getProperty("os.name");
    private static StackPane page;
    private List<MusicFolder> musicFolder = new ArrayList<>();
    private Controller controller = new Controller();

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
                    musicFolder.add(controller.handleFolder(folder));
                    controller.writeToUserFolder(folder.getAbsolutePath());
                }
            });

            fileMenu.getItems().addAll(addFolderContent);
            if (os != null && os.startsWith("Mac"))
                menuBar.useSystemMenuBarProperty().set(true);

            menuBar.getMenus().addAll(fileMenu, optionsMenu, helpMenu);
            BorderPane borderPane = new BorderPane();
            borderPane.setTop(menuBar);
            controller.addObserver(this);

            page = FXMLLoader.load(Main.class.getResource("/graphic_interface/mainWindow.fxml"));
            page.getChildren().add(borderPane);
            Scene scene = new Scene(page);

            primaryStage.setScene(scene);
            primaryStage.setTitle("Music Player");
            primaryStage.show();
            Thread t = new Thread(() -> controller.getContentFromUserFile());
            t.setDaemon(true);
            t.start();
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

    @Override
    public void update(MusicFolder newFolder) {
        musicFolder.add(newFolder);
        // Appends a new album to the collection
        Thread thread = new Thread(() -> Platform.runLater(() -> {
            try {
                StackPane pane = FXMLLoader.load(Main.class.getResource("/graphic_interface/entry.fxml"));
                AnchorPane anchorPane = (AnchorPane) pane.getChildren().get(0);
                anchorPane.setLayoutY(scroll);
                ((AnchorPane) ((ScrollPane) page.getChildren().get(0)).getContent()).getChildren().add(anchorPane);
                scroll += 200;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        thread.setDaemon(true);
        thread.start();
    }
}