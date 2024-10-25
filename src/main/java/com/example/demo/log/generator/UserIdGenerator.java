package com.example.demo.log.generator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
@Slf4j
public class UserIdGenerator {

    private static final String UNAUTHENTICATED_USER_ID = "-";

    private final RandomNumberGenerator randomNumberGenerator;

    private List<String> availableUserIds;

    public UserIdGenerator(RandomNumberGenerator randomNumberGenerator) {
        this.randomNumberGenerator = randomNumberGenerator;
        populateAvailableUserIds();
    }

    private void populateAvailableUserIds() {
        try {
            availableUserIds = Files.readAllLines(Paths.get("src/main/resources/user_ids.txt"));
            assert !availableUserIds.isEmpty() : "user_ids.txt must have at least 1 entry";
            availableUserIds.forEach(availableUserId -> {
                assert !availableUserId.isBlank() : "availableUserId must not be blank but it was - %s".formatted(availableUserId);
                assert availableUserId.length() >= 5 : "availableUserId length must be at least 5 characters long, but it is - %s".formatted(availableUserId);
                assert availableUserId.length() <= 15 : "availableUserId length must be at most 15 characters long, but it was = %s".formatted(availableUserId);
            });
        } catch (IOException | SecurityException exception) {
            log.error("Error occurred while reading user_ids.txt", exception);
            throw new RuntimeException(exception);
        }
    }

    public String generateUserId() {
        int blankUserProbability = randomNumberGenerator.generateRandomNumberInRange(1, 100);
        if (blankUserProbability <= 30) {
            return UNAUTHENTICATED_USER_ID;
        } else {
            int randomUserId = randomNumberGenerator.generateRandomNumberInRange(0, availableUserIds.size() - 1);
            return availableUserIds.get(randomUserId);
        }
    }
}
