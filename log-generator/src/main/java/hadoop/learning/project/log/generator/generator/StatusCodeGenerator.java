package hadoop.learning.project.log.generator.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StatusCodeGenerator {

    private static final List<Integer> AVAILABLE_STATUS_CODES = List.of(200, 201, 204, 301, 302, 304, 400, 401, 404, 500, 502, 503);

    private final RandomNumberGenerator randomNumberGenerator;

    public int generateStatusCode() {
        final int randomStatusCodeIndex = randomNumberGenerator.generateRandomNumberInRange(0, AVAILABLE_STATUS_CODES.size() - 1);
        return AVAILABLE_STATUS_CODES.get(randomStatusCodeIndex);
    }
}
