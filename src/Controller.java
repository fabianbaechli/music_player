import Music.MusicFile;
import Music.MusicFolder;
import ObserverPattern.Observer;
import ObserverPattern.Subject;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class Controller implements Subject {
    private Model model = new Model(Controller.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "/user_folders.txt");
    private ArrayList<Observer> observers = new ArrayList<>();

    void handleFolder(File folder) {
        int[] musicFilesParsed = {0};
        int[] musicFiles = {0};
        List<MusicFile> songs = new ArrayList<>();
        final MusicFolder[] musicFolder = new MusicFolder[1];
        String image = null;
        String folderName = folder.getName();
        Thread runnable = null;

        if (folder.listFiles() != null) {
            // Iterates over the files in the folder
            // noinspection ConstantConditions
            for (File fileFromFolder : folder.listFiles()) {
                String extension = getExtension(fileFromFolder.getName());
                if (extension.toLowerCase().equals("wav")) {
                    musicFiles[0] += 1;
                } else if (extension.toLowerCase().equals("png") || extension.toLowerCase().equals("jpg")) {
                    image = fileFromFolder.getAbsolutePath();
                }
            }

            for (File fileFromFolder : folder.listFiles()) {
                // Gets the ending of the file
                String extension = getExtension(fileFromFolder.getName());
                // If it's a music file
                if (extension.toLowerCase().equals("wav")) {
                    Media media = new Media(Paths.get(fileFromFolder.getAbsolutePath()).toUri().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                    String finalImage = image;
                    runnable = new Thread(() -> {
                        musicFilesParsed[0] += 1;
                        double songDuration = decimalToTime(media.getDuration().toMinutes());
                        MusicFile musicFile = new MusicFile(songDuration, fileFromFolder.getName(), fileFromFolder.getPath(), mediaPlayer);
                        songs.add(musicFile);

                        // ensures that every song is loaded
                        if (musicFilesParsed[0] == musicFiles[0]) {
                            assert finalImage != null;
                            musicFolder[0] = new MusicFolder(songs, Paths.get(finalImage).toUri().toString(), folderName);
                            notifyObserver(musicFolder[0]);
                        }
                    });
                    try {
                        // so that the method does not exit before all songs are loaded
                        runnable.setDaemon(true);
                        runnable.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.setOnReady(runnable);
                    // If its an image
                }
            }
        }
    }

    void writeToUserFolder(String text) {
        model.writeToFile(text);
    }

    void getContentFromUserFile() {
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

    double decimalToTime(double decimalValueInMinutes) {
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

    float songProgressOnProgressBar(double songPlayed, double songLength) {
        // minutes
        int songPlayedSeconds = (int) songPlayed;
        // seconds (already in time format)
        double temp = (songPlayed - songPlayedSeconds) * 100;
        songPlayedSeconds = (songPlayedSeconds * 60) + (int) temp;

        int songLengthSeconds = (int) songPlayed;
        temp = (songLength - songLengthSeconds) * 100;
        songLengthSeconds = (songLengthSeconds * 60) + (int) temp;

        return (float) songPlayedSeconds / (float) songLengthSeconds;
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
