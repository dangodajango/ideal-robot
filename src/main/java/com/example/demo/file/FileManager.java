package com.example.demo.file;

import com.example.demo.log.generator.LogGenerator;
import com.example.demo.log.generator.RandomNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Slf4j
@RequiredArgsConstructor
public class FileManager {

    private static final int MINIMUM_NUMBER_OF_GENERATED_LOGS = 10_000_000;

    private static final int MAXIMUM_NUMBER_OF_GENERATED_LOGS = 15_000_000;

    private final LogGenerator logGenerator;

    private final RandomNumberGenerator randomNumberGenerator;

    public Path createFile(String fileName) {
        final Path directoryPath = Paths.get("/tmp");
        assert Files.exists(directoryPath) : "directoryPath doesn't exist - %s".formatted(directoryPath);
        final Path fileNamePath = directoryPath.resolve(fileName);
        assert Files.notExists(fileNamePath) : "fileNamePath already exists - %s".formatted(fileNamePath);
        try {
            Files.createFile(fileNamePath);
            assert Files.exists(fileNamePath) : "fileNamePath was not created successfully - %s".formatted(fileNamePath);
        } catch (IOException ioException) {
            log.error("Could not create file - %s".formatted(fileNamePath), ioException);
            throw new RuntimeException(ioException);
        }
        return fileNamePath;
    }

    /**
     * It will write roughly 250-350 MBs of logs into the given file.
     */
    public void writeBatchOfLogsToFile(Path filePath) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath.toString(), true))) {
            final int randomNumberOfGeneratedLogs = randomNumberGenerator.generateRandomNumberInRange(MINIMUM_NUMBER_OF_GENERATED_LOGS, MAXIMUM_NUMBER_OF_GENERATED_LOGS);
            for (int i = 0; i < randomNumberOfGeneratedLogs; i++) {
                final String generatedLog = logGenerator.generateLog();
                assert Files.exists(filePath) : "filePath (%s) must exist before trying to write to it".formatted(filePath.toString());
                bufferedWriter.write(generatedLog);
                bufferedWriter.newLine();
            }
        } catch (IOException ioException) {
            log.error("Exception occurred while writing to a file - %s".formatted(filePath), ioException);
            throw new RuntimeException(ioException);
        }
    }

    public void moveFileToHdfs() {

    }
}
