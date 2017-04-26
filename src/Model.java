import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

class Model {
    private String fileLocation;

    Model(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    void writeToFile(String text) {
        text = "\n" + text;
        try {
            File file = new File(fileLocation);
            if (file.exists() && !file.isDirectory()) {         // Append, if the file exists
                Files.write(Paths.get(fileLocation), text.getBytes(), StandardOpenOption.APPEND);
            } else {                                            // Create File if not
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String readUserFileContent() {
        String text = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileLocation));
            String temp = bufferedReader.readLine();
            while (true) {
                if (temp != null) {
                    text += (temp + "\n");
                } else {
                    return text;
                }
                temp = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }
}
