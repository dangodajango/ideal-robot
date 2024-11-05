package hadoop.learning.project.log.generator.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResponseSizeGenerator {

    private final RandomNumberGenerator randomNumberGenerator;

    public int generateResponseSize(final String resource, final int statusCode) {
        if (statusCode != 200) {
            return randomNumberGenerator.generateRandomNumberInRange(100, 1_000);
        }
        final String verb = resource.split(" ")[0];
        switch (verb) {
            case "Get":
                return randomNumberGenerator.generateRandomNumberInRange(10_000, 10_000_000);
            case "Post":
            case "Put":
                return randomNumberGenerator.generateRandomNumberInRange(1_000, 10_000);
            case "Delete":
            case "Head":
                return randomNumberGenerator.generateRandomNumberInRange(100, 1_000);
            default:
                throw new IllegalStateException("Unexpected verb value: " + verb);
        }
    }
}
