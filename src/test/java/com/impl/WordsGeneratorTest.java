package com.impl;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class WordsGeneratorTest {

    @Test
    void justAnExample() {
        File directoryPath = new File("C:\\csv\\nov");
        // Create a new subfolder called "processed" into source directory
        try {
            Path path = Path.of(directoryPath.getAbsolutePath() + "\\processed");
            if (!Files.exists(path) || !Files.isDirectory(path)) {
                Files.createDirectory(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FilenameFilter textFileFilter = (dir, name) -> {
            String lowercaseName = name.toLowerCase();
            if (lowercaseName.endsWith(".csv")) {
                return true;
            } else {
                return false;
            }
        };
        // List of all the csv files
        File filesList[] = directoryPath.listFiles(textFileFilter);
        System.out.println("List of the text files in the specified directory:");
        for(File file : filesList) {

            // do here something with the file's data

            // Move here file into new subdirectory when file processing is finished
            Path copied = Paths.get(file.getParent() + "\\processed");
            Path originalPath = file.toPath();
            try {
                // Use resolve method to keep the "processed" as folder
                Files.move(originalPath, copied.resolve(originalPath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
