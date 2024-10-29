package hadoop.learning.project.log.generator.file;

import hadoop.learning.project.log.generator.log.generator.LogGenerator;
import hadoop.learning.project.log.generator.log.generator.RandomNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.FileSystem;
import org.springframework.beans.factory.annotation.Value;
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

    private final FileSystem hadoopFileSystem;

    @Value("${hdfs.generated-files.path}")
    private String hdfsGeneratedFilesPath;

    public Path createFile(final String fileName) {
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
    public void writeBatchOfLogsToFile(final Path filePath) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath.toString(), true))) {
            final int randomNumberOfGeneratedLogs = randomNumberGenerator.generateRandomNumberInRange(MINIMUM_NUMBER_OF_GENERATED_LOGS, MAXIMUM_NUMBER_OF_GENERATED_LOGS);
            assert Files.exists(filePath) : "filePath (%s) must exist before trying to write to it".formatted(filePath.toString());
            for (int i = 0; i < randomNumberOfGeneratedLogs; i++) {
                final String generatedLog = logGenerator.generateLog();
                bufferedWriter.write(generatedLog);
                bufferedWriter.newLine();
            }
        } catch (IOException ioException) {
            log.error("Exception occurred while writing to a file - {}", filePath, ioException);
            try {
                log.info("Cleaning up failed file {}", filePath.getFileName());
                Files.delete(filePath);
            } catch (IOException exception) {
                log.error("Exception occurred while cleaning up the file - {}", filePath.getFileName());
                throw new RuntimeException(exception);
            }
        }
    }

    public void moveFileToHdfs(final Path filePath) {
        try {
            final org.apache.hadoop.fs.Path hdfsPath = new org.apache.hadoop.fs.Path(hdfsGeneratedFilesPath);
            final org.apache.hadoop.fs.Path pathToFileInHdfs = new org.apache.hadoop.fs.Path(hdfsGeneratedFilesPath, filePath.getFileName().toString());
            assert hadoopFileSystem.exists(hdfsPath) : "hdfsPath (%s) must exist before copying to it".formatted(hdfsPath.toString());
            assert !hadoopFileSystem.exists(pathToFileInHdfs) : "file (%s) already exists in the hdfs, cannot override it".formatted(pathToFileInHdfs.toString());
            assert hadoopFileSystem.getFileStatus(hdfsPath).isDirectory() : "hdfsPath (%s) must be a path to a directory";
            assert Files.exists(filePath) : "filePath (%s) must exist before copying it to the hdfs".formatted(filePath.toString());
            hadoopFileSystem.copyFromLocalFile(new org.apache.hadoop.fs.Path(filePath.toString()), hdfsPath);
            assert hadoopFileSystem.exists(pathToFileInHdfs) : "file (%s) was not copied to the hdfs".formatted(hdfsPath.toString());
            assert Files.size(filePath) == hadoopFileSystem.getFileStatus(pathToFileInHdfs).getLen() : "file (%s) was not copied entirely to the hdfs".formatted(filePath.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
