package com.example.demo.log.generator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class ResourceGenerator {

    private static final HttpVerbs[] HTTP_VERBS = HttpVerbs.values();

    private final List<String> endpointsWithId;

    private final List<String> endpointsWithoutId;

    private final RandomNumberGenerator randomNumberGenerator;

    public ResourceGenerator(RandomNumberGenerator randomNumberGenerator) {
        this.randomNumberGenerator = randomNumberGenerator;
        try {
            endpointsWithId = Files.readAllLines(Paths.get("src/main/resources/endpoints_with_id.txt"));
            assert !endpointsWithId.isEmpty() : "endpoints_with_id.txt must have at least 1 entry";
            endpointsWithId.forEach(endpointWithId -> {
                assert !endpointWithId.isBlank() : "endpointWithId must not be blank, but it was - %s".formatted(endpointWithId);
                assert endpointWithId.contains("{id}") : "endpointWithId must contain an {id} tag, but it doesn't - %s".formatted(endpointWithId);
                assert endpointWithId.substring(1).split("/").length == 2 : "endpointWithId must contain only 2 sections, but it contains - %s (%s)".formatted(endpointWithId.substring(1).split("/").length == 2, endpointWithId);
            });

            endpointsWithoutId = Files.readAllLines(Paths.get("src/main/resources/endpoints_without_id.txt"));
            assert !endpointsWithoutId.isEmpty() : "endpoints_without_id.txt must have at least 1 entry";
            endpointsWithoutId.forEach(endpointWithoutId -> {
                assert !endpointWithoutId.isBlank() : "endpointsWithoutId must not be blank, but it was - %s".formatted(endpointsWithoutId);
                assert !endpointWithoutId.contains("{id}") : "endpointWithoutId must not contain an {id} tag, but it does - %s".formatted(endpointWithoutId);
                assert endpointWithoutId.substring(1).split("/").length == 1 : "endpointWithoutId must not have more than 1 sections in it, but it contains - %s (%s)".formatted(endpointWithoutId.substring(1).split("/").length, endpointWithoutId);
            });
        } catch (IOException exception) {
            log.error("Error occurred while retrieving the endpoints", exception);
            throw new RuntimeException("Could not import endpoints");
        }
    }

    public String generateResource() {
        int randomVerbIndex = randomNumberGenerator.generateRandomNumberInRange(0, HTTP_VERBS.length - 1);
        HttpVerbs httpVerb = HTTP_VERBS[randomVerbIndex];
        String endpoint = switch (httpVerb) {
            case PUT, DELETE -> retrieveEndpointWithId();
            case POST, HEAD -> retrieveEndpointWithoutId();
            case GET -> retrieveRandomEndpoint();
        };
        String httpVersion = retrieveRandomHttpVersion();
        return "%s %s %s".formatted(httpVerb.getVerb(), endpoint, httpVersion);
    }

    private String retrieveEndpointWithId() {
        int randomEndpointIndex = randomNumberGenerator.generateRandomNumberInRange(0, endpointsWithId.size() - 1);
        String endpointWithId = endpointsWithId.get(randomEndpointIndex);
        String randomId = UUID.randomUUID().toString();
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

    private String retrieveRandomHttpVersion() {
        int randomHttpVersionProbability = randomNumberGenerator.generateRandomNumberInRange(1, 3);
        return switch (randomHttpVersionProbability) {
            case 1 -> "HTTP/1.1";
            case 2 -> "HTTP/2";
            case 3 -> "HTTP/3";
            default ->
                    throw new IllegalStateException("Unexpected randomHttpVersionProbability value: " + randomHttpVersionProbability);
        };
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
