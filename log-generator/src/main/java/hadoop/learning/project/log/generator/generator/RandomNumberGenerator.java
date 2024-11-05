package hadoop.learning.project.log.generator.generator;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomNumberGenerator {

    private final Random randomNumberGenerator = new Random();

    public int generateRandomNumberInRange(int lowerBound, int upperBound) {
        assert lowerBound >= 0 : String.format("lowerBound must be bigger then or equal to 0, but it was - %s", lowerBound);
        assert upperBound > 0 : String.format("upperBound must be strictly bigger then 0, but it was - %s", upperBound);
        assert upperBound >= lowerBound : String.format("upperBound must be bigger then or equal to the lowerBound, but they were - %s(U) | %s(L)", upperBound, lowerBound);

        final int randomNumberInRange = randomNumberGenerator.nextInt(upperBound - lowerBound + 1) + lowerBound;

        assert randomNumberInRange >= lowerBound : String.format("randomNumberInRange must be bigger than or equal to the lowerBound(%s), but it was - %s", lowerBound, randomNumberInRange);
        assert randomNumberInRange <= upperBound : String.format("randomNumberInRange must be smaller than or equal to the upperBound(%s), but it was - %s", upperBound, randomNumberInRange);
        return randomNumberInRange;
    }
}
