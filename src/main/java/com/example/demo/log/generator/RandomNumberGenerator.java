package com.example.demo.log.generator;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomNumberGenerator {

    private final Random randomNumberGenerator = new Random();

    public int generateRandomNumberInRange(int lowerBound, int upperBound) {
        assert lowerBound >= 0 : "Lower bound must be bigger then or equal to 0, but it was - %s".formatted(lowerBound);
        assert upperBound > 0 : "Upper bound must be strictly bigger then 0, but it was - %s".formatted(upperBound);
        assert upperBound > lowerBound : "Upper bound must be bigger then the lower bound, but they were - %s(U) | %s(L)".formatted(upperBound, lowerBound);

        int randomNumberInRange = randomNumberGenerator.nextInt(upperBound - lowerBound) + lowerBound;

        assert randomNumberInRange >= lowerBound : "Generated number is smaller then the lower bound";
        assert randomNumberInRange <= upperBound : "Generated number is bigger then the upper bound";
        return randomNumberInRange;
    }
}
