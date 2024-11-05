package hadoop.learning.project.log.generator.generator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

@Component
@Slf4j
public class ResourceGenerator {

    private static final HttpVerbs[] HTTP_VERBS = HttpVerbs.values();

    private final List<String> endpointsWithId;

    private final List<String> endpointsWithoutId;

    private final RandomNumberGenerator randomNumberGenerator;

    public ResourceGenerator(final RandomNumberGenerator randomNumberGenerator) {
        this.randomNumberGenerator = randomNumberGenerator;
        try {
            endpointsWithId = Files.readAllLines(Paths.get("log-generator/src/main/resources/endpoints_with_id.txt"));
            assert !endpointsWithId.isEmpty() : "endpoints_with_id.txt must have at least 1 entry";
            endpointsWithId.forEach(endpointWithId -> {
                assert !endpointWithId.isEmpty() : "endpointWithId must not be empty";
                assert endpointWithId.contains("{id}") : format("endpointWithId must contain an {id} tag, but it doesn't - %s", endpointWithId);
                assert endpointWithId.substring(1).split("/").length == 2 : format("endpointWithId must contain only 2 sections, but it contains - %s (%s)", endpointWithId.substring(1).split("/").length == 2, endpointWithId);
            });

            endpointsWithoutId = Files.readAllLines(Paths.get("log-generator/src/main/resources/endpoints_without_id.txt"));
            assert !endpointsWithoutId.isEmpty() : "endpoints_without_id.txt must have at least 1 entry";
            endpointsWithoutId.forEach(endpointWithoutId -> {
                assert !endpointWithoutId.isEmpty() : format("endpointsWithoutId must not be empty, but it was - %s", endpointsWithoutId);
                assert !endpointWithoutId.contains("{id}") : format("endpointWithoutId must not contain an {id} tag, but it does - %s", endpointWithoutId);
                assert endpointWithoutId.substring(1).split("/").length == 1 : format("endpointWithoutId must not have more than 1 sections in it, but it contains - %s (%s)", endpointWithoutId.substring(1).split("/").length, endpointWithoutId);
            });
        } catch (IOException exception) {
            log.error("Error occurred while retrieving the endpoints", exception);
            throw new RuntimeException("Could not import endpoints");
        }
    }

    public String generateResource() {
        final int randomVerbIndex = randomNumberGenerator.generateRandomNumberInRange(0, HTTP_VERBS.length - 1);
        final HttpVerbs httpVerb = HTTP_VERBS[randomVerbIndex];
        String endpoint;
        switch (httpVerb) {
            case PUT:
            case DELETE:
                endpoint = retrieveEndpointWithId();
                break;
            case POST:
            case HEAD:
                endpoint = retrieveEndpointWithoutId();
                break;
            case GET:
                endpoint = retrieveRandomEndpoint();
                break;
            default:
                throw new IllegalStateException(format("Incorrect HTTP verb - %s", httpVerb.getVerb()));
        }
        final String httpVersion = retrieveRandomHttpVersion();
        return format("%s %s %s", httpVerb.getVerb(), endpoint, httpVersion);
    }

    private String retrieveEndpointWithId() {
        final int randomEndpointIndex = randomNumberGenerator.generateRandomNumberInRange(0, endpointsWithId.size() - 1);
        final String endpointWithId = endpointsWithId.get(randomEndpointIndex);
        final String randomId = UUID.randomUUID().toString();
        return endpointWithId.replace("{id}", randomId);
    }

    private String retrieveEndpointWithoutId() {
        final int randomEndpointIndex = randomNumberGenerator.generateRandomNumberInRange(0, endpointsWithoutId.size() - 1);
        return endpointsWithoutId.get(randomEndpointIndex);
    }

    private String retrieveRandomEndpoint() {
        final int randomEndpointProbability = randomNumberGenerator.generateRandomNumberInRange(1, 100);
        if (randomEndpointProbability <= 50) {
            return retrieveEndpointWithoutId();
        } else {
            return retrieveEndpointWithId();
        }
    }

    private String retrieveRandomHttpVersion() {
        final int randomHttpVersionProbability = randomNumberGenerator.generateRandomNumberInRange(1, 3);
        switch (randomHttpVersionProbability) {
            case 1:
                return "HTTP/1.1";
            case 2:
                return "HTTP/2";
            case 3:
                return "HTTP/3";
            default:
                throw new IllegalStateException("Unexpected randomHttpVersionProbability value: " + randomHttpVersionProbability);
        }
    }
}
