package ObserverPattern;

import Music.MusicFolder;

public interface Subject {
    void addObserver(Observer o);

    void unregisterObserver(Observer o);

    void notifyObserver(MusicFolder musicFolder);
}
