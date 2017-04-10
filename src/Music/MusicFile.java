package Music;

public class MusicFile {
    private double duration;
    private String name;
    private String path;

    public MusicFile(double duration, String name, String path) {
        this.duration = duration;
        this.name = name;
        this.path = path;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}
