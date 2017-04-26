package Model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;

public class Model {
    private String fileLocation;

    public Model(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public void writeToFile(String text) {
        try {
            File file = new File(fileLocation);
            if (file.exists() && !file.isDirectory()) {         // Append, if the file exists
                text = "\n" + text;
                Files.write(Paths.get(fileLocation), text.getBytes(), StandardOpenOption.APPEND);
            } else {                                            // Create File if not
                createFile();
                writeToFile(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFile() {
        File file = new File(fileLocation);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteLineFromUserFile(String lineName) {
        try {
            File file = new File(fileLocation);
            if (file.exists() && !file.isDirectory()) {
                ArrayList<String> lines = new ArrayList<>(Arrays.asList(readUserFileContent().split("\n")));
                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).equals(lineName)) {
                        System.out.println("found it: " + lines.get(i));
                        lines.remove(i);
                        createFile();
                        for (Object line : lines) {
                            System.out.println("wrote " + line.toString());
                            writeToFile(line.toString());
                        }
                    }
                }
            } else {
                FileWriter fileWriter = new FileWriter(fileLocation);
                fileWriter.write("");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readUserFileContent() {
        String text = "";
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(fileLocation));
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
        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }
}
