package ObserverPattern;

import Music.MusicFolder;

public interface Subject {
    public void addObserver(Observer o);

    public void unregisterObserver(Observer o);

    public void notifyObserver(String name, double songLength);
    public void notifyObserver(MusicFolder musicFolder);
}
