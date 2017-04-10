package Music;

import javafx.scene.image.Image;

import java.io.File;
import java.util.List;

public class MusicFolder {
    List<MusicFile> files;
    String imagePath;
    String folderName;

    public MusicFolder(List<MusicFile> files, String imagePath, String folderName) {
        this.files = files;
        this.imagePath = imagePath;
        this.folderName = folderName;
    }

    public List<MusicFile> getFiles() {
        return files;
    }

    public String getFolderImage() {
        return imagePath;
    }

    public String getFolderName() {
        return folderName;
    }

    public String describeFolder() {
        String listString = "";
        for (MusicFile s : files) {
            listString += "\u0009" + s.getName() + " : " + s.getDuration() + "\n";
        }
        return folderName + "\n" + "it contains these music files:" + "\n" + listString;
    }
}
