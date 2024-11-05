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

import static java.lang.String.format;

@Component
@Slf4j
@RequiredArgsConstructor
public class FileManager {

    private static final int MINIMUM_NUMBER_OF_GENERATED_LOGS = 10_000;

    private static final int MAXIMUM_NUMBER_OF_GENERATED_LOGS = 50_000;

    private final LogGenerator logGenerator;

    private final RandomNumberGenerator randomNumberGenerator;

    private final FileSystem hadoopFileSystem;

    @Value("${hdfs.generated-files.path}")
    private String hdfsGeneratedFilesPath;

    @Value("${local.generated-files.path}")
    private String localGeneratedFilesPath;

    public Path createFile(final String fileName) {
        final Path directoryPath = Paths.get(localGeneratedFilesPath);
        assert Files.exists(directoryPath) : String.format("directoryPath %s must exist before creating a file in it", directoryPath);
        final Path fileNamePath = directoryPath.resolve(fileName);
        assert Files.notExists(fileNamePath) : String.format("fileNamePath %s must be a new file, but it already exists", fileNamePath);
        try {
            Files.createFile(fileNamePath);
            assert Files.exists(fileNamePath) : String.format("fileNamePath %s was not created successfully", fileNamePath);
        } catch (IOException ioException) {
            log.error("Could not create file {}", fileNamePath.getFileName(), ioException);
            throw new RuntimeException(ioException);
        }
        return fileNamePath;
    }

    public void writeBatchOfLogsToFile(final Path filePath) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath.toString(), true))) {
            final int randomNumberOfGeneratedLogs = randomNumberGenerator.generateRandomNumberInRange(MINIMUM_NUMBER_OF_GENERATED_LOGS, MAXIMUM_NUMBER_OF_GENERATED_LOGS);
            log.info("Generating {} number of logs in file {}", randomNumberOfGeneratedLogs, filePath.getFileName());
            assert Files.exists(filePath) : String.format("filePath %s must exist before trying to write to it", filePath);
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
            assert hadoopFileSystem.exists(hdfsPath) : format("hdfsPath %s must exist before copying to it", hdfsPath);
            assert !hadoopFileSystem.exists(pathToFileInHdfs) : format("file %s already exists in the hdfs, cannot override it", pathToFileInHdfs);
            assert hadoopFileSystem.getFileStatus(hdfsPath).isDirectory() : "hdfsPath %s must be a path to a directory";
            assert Files.exists(filePath) : format("filePath %s must exist before copying it to the hdfs", filePath);
            hadoopFileSystem.copyFromLocalFile(new org.apache.hadoop.fs.Path(filePath.toString()), hdfsPath);
            assert hadoopFileSystem.exists(pathToFileInHdfs) : format("file %s was not copied to the hdfs successfully", hdfsPath);
            assert Files.size(filePath) == hadoopFileSystem.getFileStatus(pathToFileInHdfs).getLen() : format("file %s was not copied entirely to the hdfs", filePath);
            Files.delete(filePath);
            assert !Files.exists(filePath) : format("file (%s) has not been deleted successfully after it has been transferred to the HDFS", filePath.getFileName().toString());
            log.info("File {} moved successfully to the HDFS", filePath.getFileName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
