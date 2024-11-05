package hadoop.learning.project.log.generator.generator;

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
        final int randomIncrement = randomNumberGenerator.generateRandomNumberInRange(1, 5000);
        if (currentTimestamp == null) {
            currentTimestamp = STARTING_TIMESTAMP.plus(Duration.ofMillis(randomIncrement));
            assert currentTimestamp.isAfter(STARTING_TIMESTAMP) : String.format("currentTimestamp must be after the startingTimestamp, but it was - %s, starting - %s",
                    currentTimestamp.format(TIMESTAMP_FORMAT), STARTING_TIMESTAMP.format(TIMESTAMP_FORMAT));
        } else {
            final LocalDateTime incrementedTimestamp = currentTimestamp.plus(Duration.ofMillis(randomIncrement));
            assert incrementedTimestamp.isAfter(currentTimestamp) : String.format("incrementedTimestamp must be after the previous timestamp, but it was - %s, previous - %s",
                    incrementedTimestamp.format(TIMESTAMP_FORMAT), currentTimestamp.format(TIMESTAMP_FORMAT));
            currentTimestamp = incrementedTimestamp;
        }
        final String generatedTimestamp = currentTimestamp.format(TIMESTAMP_FORMAT);
        assert generatedTimestamp.matches("\\d{2}/[A-Za-z]{3}/\\d{4}:\\d{2}:\\d{2}:\\d{2}") : String.format("generatedTimestamp is not valid - %s", generatedTimestamp);
        return generatedTimestamp;
    }
}
