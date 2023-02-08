package com.wordscore.engine.processor;

import com.opencsv.bean.CsvToBeanBuilder;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class DataValidationCheckJob implements Job {

    public DataValidationCheckJob() {
    }

    @Override
    public void execute(JobExecutionContext context) {

        File directoryPath = new File("C:\\csv\\nov");
        // Create a new subfolder called "processed" into source directory
        try {
            Path processedFolderPath = Path.of(directoryPath.getAbsolutePath() + "/processed");
            if (!Files.exists(processedFolderPath) || !Files.isDirectory(processedFolderPath)) {
                Files.createDirectory(processedFolderPath);
            }

            Path invalidFilesFolderPath = Path.of(directoryPath.getAbsolutePath() + "/invalid_files");
            if (!Files.exists(invalidFilesFolderPath) || !Files.isDirectory(invalidFilesFolderPath)) {
                Files.createDirectory(invalidFilesFolderPath);
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

            try {
                try (var br = new FileReader(file.getAbsolutePath(), StandardCharsets.UTF_16)){
                    List<CsvLine> beans = new CsvToBeanBuilder(br)
                            .withType(CsvLine.class)
                            .withSeparator('\t')
                            .withSkipLines(3)
                            .build()
                            .parse();

                    Path originalPath = null;

                    boolean found50 = false;
                    boolean found500 = false;
                    boolean found5000 = false;

                    for (CsvLine item : beans)
                    {
                        originalPath = file.toPath();

                        // Its possible column "Avg. monthly searches" to have empty value
                        if (item.getScore() != null)
                        {
                            if (item.getScore().compareTo(BigDecimal.valueOf(50)) == 0)
                            {
                                found50 = true;
                            }

                            if (item.getScore().compareTo(BigDecimal.valueOf(500)) == 0)
                            {
                                found500 = true;
                            }

                            if (item.getScore().compareTo(BigDecimal.valueOf(5000)) == 0)
                            {
                                found5000 = true;
                            }
                        }

                        if(found50 == true && found500 == true && found5000 == true){

                            found50 = false;
                            found500 = false;
                            found5000 = false;

                            // Move here file into new subdirectory when file is invalid
                            Path copied = Paths.get(file.getParent() + "/invalid_files");
                            try {
                                // Use resolve method to keep the "processed" as folder
                                br.close();
                                Files.move(originalPath, copied.resolve(originalPath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                                break;
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    if (file.exists())
                    {
                        // Move here file into new subdirectory when file processing is finished
                        Path copied = Paths.get(file.getParent() + "/processed");
                        try {
                            // Use resolve method to keep the "processed" as folder
                            br.close();
                            Files.move(originalPath, copied.resolve(originalPath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                            break;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            Path originalPath = file.toPath();
            System.out.println(String.format("\nProcessed file : %s, moving the file to subfolder /processed\n",
                    originalPath));
        }
    }
}
