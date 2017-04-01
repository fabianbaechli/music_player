package ObserverPattern;

import Music.MusicFolder;

public interface Observer {
    public void update(String name, double songLength);
    public void update(MusicFolder musicFolder);
}
