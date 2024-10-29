package hadoop.learning.project.log.generator;

import hadoop.learning.project.log.generator.file.FileManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Path;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class DemoApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    private final FileManager fileManager;

    @Override
    public void run(ApplicationArguments args) {
        final Path filePath = fileManager.createFile("test12.txt");
        fileManager.writeBatchOfLogsToFile(filePath);
        fileManager.moveFileToHdfs(filePath);
    }
}
