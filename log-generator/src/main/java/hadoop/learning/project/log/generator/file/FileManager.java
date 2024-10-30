package hadoop.learning.project.log.generator.file;

import hadoop.learning.project.log.generator.LogGenerator;
import hadoop.learning.project.log.generator.generator.RandomNumberGenerator;
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

    @Value("${local.generated-files.path}")
    private String localGeneratedFilesPath;

    public Path createFile(final String fileName) {
        final Path directoryPath = Paths.get(localGeneratedFilesPath);
        assert Files.exists(directoryPath) : "directoryPath %s must exist before creating a file in it".formatted(directoryPath);
        final Path fileNamePath = directoryPath.resolve(fileName);
        assert Files.notExists(fileNamePath) : "fileNamePath %s must be a new file, but it already exists".formatted(fileNamePath);
        try {
            Files.createFile(fileNamePath);
            assert Files.exists(fileNamePath) : "fileNamePath %s was not created successfully".formatted(fileNamePath);
        } catch (IOException ioException) {
            log.error("Could not create file {}", fileNamePath.getFileName(), ioException);
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
            log.info("Generating {} number of logs in file {}", randomNumberOfGeneratedLogs, filePath.getFileName());
            assert Files.exists(filePath) : "filePath %s must exist before trying to write to it".formatted(filePath.toString());
            for (int i = 0; i < randomNumberOfGeneratedLogs; i++) {
                final String generatedLog = logGenerator.generateLog();
                bufferedWriter.write(generatedLog);
                bufferedWriter.newLine();
            }
        } catch (IOException ioException) {
            log.error("Exception occurred while writing to a file - {}", filePath.getFileName(), ioException);
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
            log.info("Moving file {} to the HDFS", filePath.getFileName());
            final org.apache.hadoop.fs.Path hdfsPath = new org.apache.hadoop.fs.Path(hdfsGeneratedFilesPath);
            final org.apache.hadoop.fs.Path pathToFileInHdfs = new org.apache.hadoop.fs.Path(hdfsGeneratedFilesPath, filePath.getFileName().toString());
            assert hadoopFileSystem.exists(hdfsPath) : "hdfsPath %s must exist before copying to it".formatted(hdfsPath.toString());
            assert !hadoopFileSystem.exists(pathToFileInHdfs) : "file %s already exists in the hdfs, cannot override it".formatted(pathToFileInHdfs.toString());
            assert hadoopFileSystem.getFileStatus(hdfsPath).isDirectory() : "hdfsPath %s must be a path to a directory";
            assert Files.exists(filePath) : "filePath %s must exist before copying it to the hdfs".formatted(filePath.toString());
            hadoopFileSystem.copyFromLocalFile(new org.apache.hadoop.fs.Path(filePath.toString()), hdfsPath);
            assert hadoopFileSystem.exists(pathToFileInHdfs) : "file %s was not copied to the hdfs successfully".formatted(hdfsPath.toString());
            assert Files.size(filePath) == hadoopFileSystem.getFileStatus(pathToFileInHdfs).getLen() : "file %s was not copied entirely to the hdfs".formatted(filePath.toString());
            Files.delete(filePath);
            assert !Files.exists(filePath) : "file (%s) has not been deleted successfully after it has been transferred to the HDFS".formatted(filePath.getFileName().toString());
            log.info("File {} moved successfully to the HDFS", filePath.getFileName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
