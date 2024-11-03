package hadoop.learning.project.log.generator;

import hadoop.learning.project.log.generator.file.FileManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Path;
import java.util.UUID;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class Main implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    private final FileManager fileManager;

    @Override
    public void run(ApplicationArguments args) {
        for (int i = 0; i < 5; i++) {
            String fileName = UUID.randomUUID() + ".txt";
            log.info("Creating file {} locally", fileName);
            final Path filePath = fileManager.createFile(fileName);
            log.info("Writing logs to file {}", fileName);
            fileManager.writeBatchOfLogsToFile(filePath);
            fileManager.moveFileToHdfs(filePath);
        }
    }
}
