package Music;

import javafx.scene.media.Media;

public class MusicFile {
    private double duration;
    private String name;
    private Media file;

    public MusicFile(double duration, String name, Media file) {
        this.duration = duration;
        this.name = name;
        this.file = file;
    }
}
