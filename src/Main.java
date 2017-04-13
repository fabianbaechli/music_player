import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import Music.MusicFile;
import Music.MusicFolder;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import com.jfoenix.controls.*;
import javafx.util.Duration;

public class Main extends Application implements ObserverPattern.Observer {
    /*
    TODO:   - Implement Song Playing view + Play Song on click
     */
    private int scroll = 0;
    private static final String os = System.getProperty("os.name");
    private static StackPane page;
    private Controller controller = new Controller();
    private MediaPlayer currentSong = null;
    private Timeline progressBarTimeline = null;

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
                    Thread thread = new Thread(() -> controller.handleFolder(folder));
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

    public void update(MusicFolder newFolder) {
        System.out.println("got folder :" + newFolder.describeFolder());
        final int[] count = {1};
        final int[] rowCounter = {3};
        // Appends a new album to the collection
        Thread thread = new Thread(() -> Platform.runLater(() -> {
            try {
                StackPane pane = FXMLLoader.load(Main.class.getResource("/graphic_interface/entry.fxml"));
                AnchorPane anchorPane = (AnchorPane) pane.getChildren().get(0);
                anchorPane.setLayoutY(scroll);

                BorderPane groundBorderPane = (BorderPane) page.getChildren().get(1);
                ScrollPane groundScrollPane = (ScrollPane) groundBorderPane.getCenter();
                groundScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                AnchorPane groundAnchorPane = (AnchorPane) groundScrollPane.getContent();
                groundAnchorPane.getChildren().add(anchorPane);

                GridPane songGrid = (GridPane) anchorPane.getChildren().get(1);
                ImageView imageView = (ImageView) anchorPane.getChildren().get(0);
                imageView.setImage(new Image(newFolder.getFolderImage()));

                for (MusicFile aMusicFile : newFolder.getFiles()) {
                    Label name = new Label(aMusicFile.getName());
                    Label album = new Label(newFolder.getFolderName());
                    String durationStr = Double.toString(aMusicFile.getDuration());
                    Label duration = new Label();

                    if (durationStr.split("\\.")[1].length() == 1) {
                        duration.setText(durationStr + "0");
                    } else {
                        duration.setText(durationStr);
                    }

                    songGrid.add(name, 0, count[0]);
                    songGrid.add(album, 1, count[0]);
                    songGrid.add(duration, 2, count[0]);

                    songGrid.getChildren().get(rowCounter[0]).setId(aMusicFile.getPath());
                    songGrid.getChildren().get(rowCounter[0] + 1).setId(aMusicFile.getPath());
                    songGrid.getChildren().get(rowCounter[0] + 2).setId(aMusicFile.getPath());
                    rowCounter[0] += 3;
                    count[0] += 1;
                }
                for (int i = 0; i < songGrid.getChildren().size(); i += 1) {
                    ((Label) songGrid.getChildren().get(i)).setPrefWidth(250);
                    ((Label) songGrid.getChildren().get(i)).setPrefHeight(250);

                    int finalI = i;
                    Runnable t = new Thread(() -> {
                        for (MusicFile aMusicFile : newFolder.getFiles()) {
                            if (aMusicFile.getPath().equals(songGrid.getChildren().get(finalI).getId())) {
                                if (currentSong != null) {
                                    currentSong.stop();
                                }
                                AnchorPane songAnchorPane = null;
                                ProgressBar progressBar = null;
                                try {
                                    StackPane songPane = FXMLLoader.load(Main.class.getResource("/graphic_interface/songPlaying.fxml"));
                                    songAnchorPane = (AnchorPane) songPane.getChildren().get(0);
                                    ImageView songCover = (ImageView) songAnchorPane.getChildren().get(0);
                                    Label songTitle = (Label) songAnchorPane.getChildren().get(1);
                                    Label songAlbum = (Label) songAnchorPane.getChildren().get(2);
                                    progressBar = (JFXProgressBar) songAnchorPane.getChildren().get(3);

                                    songCover.setImage(imageView.getImage());
                                    songTitle.setText(aMusicFile.getName());
                                    songAlbum.setText(newFolder.getFolderName());
                                    groundBorderPane.setBottom(songPane);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                currentSong = aMusicFile.getPlayer();
                                currentSong.play();
                                if (progressBarTimeline != null) {
                                    progressBarTimeline.stop();
                                }
                                AnchorPane finalSongPane = songAnchorPane;
                                ProgressBar finalProgressBar = progressBar;
                                progressBarTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                                    double currentSongLength = aMusicFile.getDuration();
                                    double currentSongPosition = controller.decimalToTime(currentSong.getCurrentTime().toMinutes());
                                    assert finalSongPane != null;
                                    ((Label) finalSongPane.getChildren().get(4)).setText(Double.toString(currentSongPosition));
                                    ((Label) finalSongPane.getChildren().get(5)).setText(Double.toString(currentSongLength));
                                    assert finalProgressBar != null;
                                    finalProgressBar.setProgress(controller.songProgressOnProgressBar(currentSongPosition, currentSongLength));
                                }));
                                progressBarTimeline.setCycleCount(-1);
                                progressBarTimeline.play();
                            }
                        }
                    });
                    songGrid.getChildren().get(i).setOnMouseClicked(event -> t.run());
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