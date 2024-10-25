package com.example.demo.log.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class TimestampGenerator {

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss");

    private static final LocalDateTime STARTING_TIMESTAMP = LocalDateTime.now();

    private final RandomNumberGenerator randomNumberGenerator;

    private LocalDateTime currentTimestamp;

    public String generateTimestamp() {
        int randomIncrement = randomNumberGenerator.generateRandomNumberInRange(1, 5000);
        if (currentTimestamp == null) {
            currentTimestamp = STARTING_TIMESTAMP.plus(Duration.ofMillis(randomIncrement));
        } else {
            LocalDateTime incrementedTimestamp = currentTimestamp.plus(Duration.ofMillis(randomIncrement));
            assert incrementedTimestamp.isAfter(currentTimestamp) : "The incremented timestamp must be after the previous timestamp, but it was - %s, previous - %s"
                    .formatted(incrementedTimestamp.format(TIMESTAMP_FORMAT), currentTimestamp.format(TIMESTAMP_FORMAT));
            currentTimestamp = incrementedTimestamp;
        }
        String generatedTimestamp = currentTimestamp.format(TIMESTAMP_FORMAT);
        assert generatedTimestamp.matches("\\d{2}/[A-Za-z]{3}/\\d{4}:\\d{2}:\\d{2}:\\d{2}") : "The generated timestamp is not valid - %s".formatted(generatedTimestamp);
        return generatedTimestamp;
    }
}
