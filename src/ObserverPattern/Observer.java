package ObserverPattern;

import Music.MusicFolder;

public interface Observer {
    void update(MusicFolder musicFolder);
    void delete(String folder);
}
