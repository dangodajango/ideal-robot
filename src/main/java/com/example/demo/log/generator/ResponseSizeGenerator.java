package com.example.demo.log.generator;

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
        return switch (verb) {
            case "Get" -> randomNumberGenerator.generateRandomNumberInRange(10_000, 10_000_000);
            case "Post", "Put" -> randomNumberGenerator.generateRandomNumberInRange(1_000, 10_000);
            case "Delete", "Head" -> randomNumberGenerator.generateRandomNumberInRange(100, 1_000);
            default -> throw new IllegalStateException("Unexpected verb value: " + verb);
        };
    }
}
