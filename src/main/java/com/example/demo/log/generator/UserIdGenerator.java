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

    private static final String ALPHANUMERIC_CHARACTERS = """
            ABCDEFGHIJKLMNOPQRSTUVWXYZ\
            abcdefghijklmnopqrstuvwxyz\
            0123456789""";

    private final RandomNumberGenerator randomNumberGenerator;

    private List<String> availableUserIds;

    public UserIdGenerator(RandomNumberGenerator randomNumberGenerator) {
        this.randomNumberGenerator = randomNumberGenerator;
        populateAvailableUserIds();
    }

    private void populateAvailableUserIds() {
        try {
            availableUserIds = Files.readAllLines(Paths.get("src/main/resources/user_ids.txt"));
            assert !availableUserIds.isEmpty() : "AvailableUserIds should have at least 1 entry in the list";
        } catch (IOException | SecurityException exception) {
            log.error("Error occurred while reading user_ids.txt", exception);
        }
    }

    public String generateUserId() {
        int blankUserProbability = randomNumberGenerator.generateRandomNumberInRange(1, 100);
        if (blankUserProbability <= 30) {
            return UNAUTHENTICATED_USER_ID;
        } else {
            return generateAuthenticatedUserId();
        }
    }

    private String generateAuthenticatedUserId() {
        int randomUserId = randomNumberGenerator.generateRandomNumberInRange(0, availableUserIds.size() - 1);
        String randomUser = availableUserIds.get(randomUserId);
        assertRetrievedUserId(randomUser);
        return randomUser;
    }

    private void assertRetrievedUserId(String generatedUserId) {
        assert generatedUserId.length() >= 5 && generatedUserId.length() <= 15 :
                "Generated authenticated user id should be between 5 and 15 characters long, but it was - %s(%s)".formatted(generatedUserId.length(), generatedUserId);
        for (char character : generatedUserId.toCharArray()) {
            assert ALPHANUMERIC_CHARACTERS.contains(Character.toString(character));
        }
    }
}
