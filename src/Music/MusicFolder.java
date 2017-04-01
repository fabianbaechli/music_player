package Music;

import javafx.scene.image.Image;

import java.io.File;
import java.util.List;

public class MusicFolder {
    List<MusicFile> files;
    Image folderImage;
    String folderName;

    public MusicFolder(List<MusicFile> files, Image folderImage, String folderName) {
        this.files = files;
        this.folderImage = folderImage;
        this.folderName = folderName;
    }

    public List<MusicFile> getFiles() {
        return files;
    }

    public Image getFolderImage() {
        return folderImage;
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
