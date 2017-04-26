package Music;

import javafx.scene.media.MediaPlayer;

public class MusicFile {
    private double duration;
    private String name;
    private String path;
    private MediaPlayer player;

    public MusicFile(double duration, String name, String path, MediaPlayer player) {
        this.player = player;
        this.duration = duration;
        this.name = name;
        this.path = path;
    }

    public double getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public String getPath() {
        return path;
    }
}
