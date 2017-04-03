import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import Music.MusicFile;
import Music.MusicFolder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
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
                    Thread thread = new Thread(() -> musicFolder.add(controller.handleFolder(folder)));
                    thread.setDaemon(true);
                    thread.start();
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
            page.getChildren().get(0).toFront();

            Thread thread = new Thread(() -> controller.getContentFromUserFile());
            thread.setDaemon(true);
            thread.start();
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
        final int[] count = {1};
        musicFolder.add(newFolder);
        // Appends a new album to the collection
        Thread thread = new Thread(() -> Platform.runLater(() -> {
            try {
                StackPane pane = FXMLLoader.load(Main.class.getResource("/graphic_interface/entry.fxml"));
                AnchorPane anchorPane = (AnchorPane) pane.getChildren().get(0);
                System.out.println(anchorPane.getHeight());
                anchorPane.setLayoutY(scroll);
                ((AnchorPane) ((ScrollPane) page.getChildren().get(1)).getContent()).getChildren().add(anchorPane);
                System.out.println(anchorPane.getChildren());
                GridPane songGrid = (GridPane) anchorPane.getChildren().get(1);
                for (MusicFile aMusicFile : newFolder.getFiles()) {
                    Label name = new Label(aMusicFile.getName());
                    Label album = new Label(newFolder.getFolderName());
                    Label duration = new Label(Double.toString(aMusicFile.getDuration()));

                    songGrid.addRow(count[0], name);
                    songGrid.addRow(count[0], album);
                    songGrid.addRow(count[0], duration);
                    count[0] += 1;
                }
                scroll += (count[0] * 18) + 40;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        thread.setDaemon(true);
        thread.start();
    }
}