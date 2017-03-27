import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import Music.MusicFile;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Main extends Application {
    private static final String os = System.getProperty("os.name");
    private static StackPane page;
    private static List<MusicFile> songs;

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
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File folder = directoryChooser.showDialog(primaryStage);
                if (folder.listFiles() != null) {
                    for (File aMusicFile : folder.listFiles()) {
                        Media media = new Media(Paths.get(aMusicFile.getAbsolutePath()).toUri().toString());
                        MediaPlayer mediaPlayer = new MediaPlayer(media);
                        mediaPlayer.setOnReady(() -> {
                            MusicFile musicFile = new MusicFile(decimalToTime(media.getDuration().toMinutes()), aMusicFile.getName(), media);
                            songs.add(musicFile);
                        });
                    }
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

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    // TODO: Change name
    private double decimalToTime(double decimalValueInMinutes) {
        int minutes = (int) decimalValueInMinutes / 60;
        double seconds = (decimalValueInMinutes - minutes) * 0.6;
        while (seconds > 0.6) {
            minutes += 1;
            seconds -= 0.6;
        }
        return round(minutes + seconds, 2);
    }
}