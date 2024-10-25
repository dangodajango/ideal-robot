package com.example.demo.log.generator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
@Slf4j
public class ResourceGenerator {

    private static final HttpVerbs[] HTTP_VERBS = HttpVerbs.values();

    private final List<String> endpointsWithId;

    private final List<String> endpointsWithoutId;

    private final RandomNumberGenerator randomNumberGenerator;

    private final RandomStringGenerator randomStringGenerator;

    public ResourceGenerator(RandomNumberGenerator randomNumberGenerator, RandomStringGenerator randomStringGenerator) {
        this.randomNumberGenerator = randomNumberGenerator;
        this.randomStringGenerator = randomStringGenerator;
        try {
            endpointsWithId = Files.readAllLines(Paths.get("src/main/resources/endpoints_with_id.txt"));
            assert !endpointsWithId.isEmpty() : "endpoints_with_id.txt must have at least 1 entry";
            endpointsWithId.forEach(endpointWithId -> {
                assert !endpointWithId.isBlank() : "endpointWithId must not be blank, but it was - %s".formatted(endpointWithId);
                assert endpointWithId.contains("{id}") : "endpointWithId must contain an {id} tag, but it doesn't - %s".formatted(endpointWithId);
                assert endpointWithId.split("/").length == 2 : "endpointWithId must contain only 2 sections, but it does - %s".formatted(endpointWithId);
            });

            endpointsWithoutId = Files.readAllLines(Paths.get("src/main/resources/endpoints_without_id.txt"));
            assert !endpointsWithoutId.isEmpty() : "endpoints_without_id.txt must have at least 1 entry";
            endpointsWithoutId.forEach(endpointWithoutId -> {
                assert !endpointWithoutId.isBlank() : "endpointsWithoutId must not be blank, but it was - %s".formatted(endpointsWithoutId);
                assert !endpointWithoutId.contains("{id}") : "endpointWithoutId must not contain an {id} tag, but it does - %s".formatted(endpointWithoutId);
                assert endpointWithoutId.split("/").length == 1 : "endpointWithoutId must not have more than 1 sections in it, but it does - %s".formatted(endpointWithoutId);
            });
        } catch (IOException exception) {
            log.error("Error occurred while retrieving the endpoints", exception);
            throw new RuntimeException("Could not import endpoints");
        }
    }

    public void generateResource() {
        int randomVerbIndex = randomNumberGenerator.generateRandomNumberInRange(0, HTTP_VERBS.length - 1);
        HttpVerbs httpVerb = HTTP_VERBS[randomVerbIndex];
        String endpoint = switch (httpVerb) {
            case POST, HEAD -> retrieveEndpointWithoutId();
            case GET -> retrieveRandomEndpoint();
            case PUT, DELETE -> retrieveEndpointWithId();
        };
    }

    private String retrieveEndpointWithId() {
        int randomEndpointIndex = randomNumberGenerator.generateRandomNumberInRange(0, endpointsWithId.size() - 1);
        String endpointWithId = endpointsWithId.get(randomEndpointIndex);
        String randomId = randomStringGenerator.generateRandomString();
        return endpointWithId.replace("{id}", randomId);
    }

    private String retrieveEndpointWithoutId() {
        int randomEndpointIndex = randomNumberGenerator.generateRandomNumberInRange(0, endpointsWithoutId.size() - 1);
        return endpointsWithoutId.get(randomEndpointIndex);
    }

    private String retrieveRandomEndpoint() {
        int randomEndpointProbability = randomNumberGenerator.generateRandomNumberInRange(1, 100);
        if (randomEndpointProbability <= 50) {
            return retrieveEndpointWithoutId();
        } else {
            return retrieveEndpointWithId();
        }
    }
}

@RequiredArgsConstructor
@Getter
enum HttpVerbs {
    GET("Get"),
    POST("Post"),
    PUT("Put"),
    DELETE("Delete"),
    HEAD("Head");

    private final String verb;
}
