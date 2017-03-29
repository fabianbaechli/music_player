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

    MusicFolder handleFolder(File folder) {
        List<MusicFile> songs = new ArrayList<>();
        MusicFolder musicFolder;
        Image image = null;
        String folderName = folder.getName();

        if (folder.listFiles() != null) {
            // Iterates over the files in the folder
            // noinspection ConstantConditions
            for (File fileFromFolder : folder.listFiles()) {
                // Gets the ending of the file
                String extension = getExtension(fileFromFolder.getName());
                // If it's a music file
                if (!extension.equals("") && extension.toLowerCase().equals("wav")) {
                    Media media = new Media(Paths.get(fileFromFolder.getAbsolutePath()).toUri().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                    mediaPlayer.setOnReady(() -> {
                        double songDuration = decimalToTime(media.getDuration().toMinutes());
                        MusicFile musicFile = new MusicFile(songDuration, fileFromFolder.getName(), fileFromFolder.getPath());
                        songs.add(musicFile);
                        notifyObserver(musicFile.getName(), songDuration);
                        System.out.println("notified observers for song:" + musicFile.getName());
                    });
                    // If its an image
                } else if (!extension.equals("") && (extension.toLowerCase().equals("jpeg") ||
                        extension.equals("png"))) {
                    image = new Image(fileFromFolder.getAbsolutePath());
                }
            }

            musicFolder = new MusicFolder(songs, image, folderName);
            return musicFolder;
        }
        return null;
    }

    public void writeToUserFolder(String text) {
        model.writeToFile(text);
    }

    public List<MusicFolder> getContentFromUserFile() {
        List<MusicFolder> folder = new ArrayList<>();
        Model model = new Model("user_folders.txt");
        String[] userFileContent = model.readUserFileContent().split("\n");
        for (String aMusicFolder : userFileContent) {
            File file = new File(aMusicFolder);
            folder.add(handleFolder(file));
        }
        return folder;
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

    public void notifyObserver(String name, double songLength) {
        for (Observer observerToNotify : observers) {
            observerToNotify.update(name, songLength);
        }
    }
}
