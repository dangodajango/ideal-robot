package hadoop.learning.project.log.generator.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IpAddressGenerator {

    private static final int OCTET_LOWER_BOUND = 0;

    private static final int OCTET_UPPER_BOUND = 255;

    private final RandomNumberGenerator randomNumberGenerator;

    public String generateIpAddress() {
        final int firstOctet = randomNumberGenerator.generateRandomNumberInRange(OCTET_LOWER_BOUND, OCTET_UPPER_BOUND);
        final int secondOctet = randomNumberGenerator.generateRandomNumberInRange(OCTET_LOWER_BOUND, OCTET_UPPER_BOUND);
        final int thirdOctet = randomNumberGenerator.generateRandomNumberInRange(OCTET_LOWER_BOUND, OCTET_UPPER_BOUND);
        final int forthOctet = randomNumberGenerator.generateRandomNumberInRange(OCTET_LOWER_BOUND, OCTET_UPPER_BOUND);
        return String.format("%s.%s.%s.%s", firstOctet, secondOctet, thirdOctet, forthOctet);
    }
}
