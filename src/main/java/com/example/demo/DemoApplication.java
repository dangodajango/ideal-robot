package com.example.demo;

import com.example.demo.log.generator.LogGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class DemoApplication implements ApplicationRunner {

    private final LogGenerator logGenerator;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        for (int i = 0; i < 20; i++) {
            log.info(logGenerator.generateLog());
        }
    }
}
