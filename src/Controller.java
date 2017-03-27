import Music.MusicFile;
import Music.MusicFolder;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class Controller {
    private Model model = new Model("user_folders.txt");

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
                int i = fileFromFolder.getName().lastIndexOf('.');
                String extension = "";
                if (i > 0) {
                    extension = fileFromFolder.getName().substring(i + 1);
                }
                // If it's a music file
                if (!extension.equals("") && extension.toLowerCase().equals("wav")) {
                    Media media = new Media(Paths.get(fileFromFolder.getAbsolutePath()).toUri().toString());
                    synchronized (this) {
                        double songDuration = decimalToTime(media.getDuration().toMinutes());
                        MusicFile musicFile = new MusicFile(songDuration, fileFromFolder.getName(), fileFromFolder.getPath());
                        songs.add(musicFile);
                    }
                    // If its an image
                } else if (!extension.equals("") && (extension.toLowerCase().equals("jpeg") ||
                        extension.equals("png"))) {
                    image = new Image(fileFromFolder.getAbsolutePath());
                }
            }
            model.writeToFile(folder.getAbsolutePath());
            musicFolder = new MusicFolder(songs, image, folderName);
            return musicFolder;
        }
        return null;
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
}
