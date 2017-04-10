import Music.MusicFile;
import Music.MusicFolder;
import ObserverPattern.Observer;
import ObserverPattern.Subject;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class Controller implements Subject {
    private Model model = new Model("user_folders.txt");
    private ArrayList<Observer> observers = new ArrayList<>();

    void handleFolder(File folder) {
        int musicFilesParsed = 0;
        List<MusicFile> songs = new ArrayList<>();
        final MusicFolder[] musicFolder = new MusicFolder[1];
        Image image = null;
        String folderName = folder.getName();

        if (folder.listFiles() != null) {
            // Iterates over the files in the folder
            // noinspection ConstantConditions
            int musicFiles = 0;
            for (File fileFromFolder : folder.listFiles()) {
                String extension = getExtension(fileFromFolder.getName());
                if (extension.toLowerCase().equals("wav")) {
                    musicFiles ++;
                }
            }
            for (File fileFromFolder : folder.listFiles()) {
                // Gets the ending of the file
                String extension = getExtension(fileFromFolder.getName());
                // If it's a music file
                if (extension.toLowerCase().equals("wav")) {
                    Media media = new Media(Paths.get(fileFromFolder.getAbsolutePath()).toUri().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                    int finalMusicFiles = musicFiles;
                    Image finalImage = image;
                    mediaPlayer.setOnReady(() -> {
                        final int musicFilesParsedCopy = musicFilesParsed + 1;
                        double songDuration = decimalToTime(media.getDuration().toMinutes());
                        MusicFile musicFile = new MusicFile(songDuration, fileFromFolder.getName(), fileFromFolder.getPath());
                        songs.add(musicFile);
                        System.out.println(musicFile.getName() + " ready");

                        System.out.println(musicFilesParsedCopy);
                        if (musicFilesParsedCopy == finalMusicFiles) {
                            musicFolder[0] = new MusicFolder(songs, finalImage, folderName);
                            notifyObserver(musicFolder[0]);
                        }
                    });
                    // If its an image
                } else if (!extension.equals("") && (extension.toLowerCase().equals("jpeg") ||
                        extension.equals("png"))) {
                    image = new Image(fileFromFolder.getAbsolutePath());
                }
            }
        }
    }

    void writeToUserFolder(String text) {
        model.writeToFile(text);
    }

    void getContentFromUserFile() {
        Model model = new Model("user_folders.txt");
        String[] userFileContent = model.readUserFileContent().split("\n");
        for (String aMusicFolder : userFileContent) {
            System.out.println("parsing folder " + aMusicFolder + " from user config file");
            File file = new File(aMusicFolder);
            handleFolder(file);
        }
    }

    private double round(double value) {
        long factor = (long) Math.pow(10, 2);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private double decimalToTime(double decimalValueInMinutes) {
        int minutes = (int) decimalValueInMinutes / 60;
        double seconds = (decimalValueInMinutes - minutes) * 0.6;
        while (seconds > 0.6) {
            minutes += 1;
            seconds -= 0.6;
        }
        return round(minutes + seconds);
    }

    private String getExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        String extension = "";
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    public void addObserver(Observer newObserver) {
        observers.add(newObserver);
    }

    public void unregisterObserver(Observer observerToDelete) {
        int observerIndex = observers.indexOf(observerToDelete);
        observers.remove(observerToDelete);
        System.out.println("Observer " + (observerIndex - 1) + " deleted");
    }

    @Override
    public void notifyObserver(MusicFolder musicFolder) {
        for (Observer observerToNotify : observers) {
            observerToNotify.update(musicFolder);
        }
    }
}
