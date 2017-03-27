package Music;

import javafx.scene.image.Image;
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
}
