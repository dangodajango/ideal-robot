package com.example.demo.log.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IpAddressGenerator {

    private static final int OCTET_LOWER_BOUND = 0;

    private static final int OCTET_UPPER_BOUND = 255;

    private final RandomNumberGenerator randomNumberGenerator;

    public String generateIpAddress() {
        int firstOctet = randomNumberGenerator.generateRandomNumberInRange(OCTET_LOWER_BOUND, OCTET_UPPER_BOUND);
        int secondOctet = randomNumberGenerator.generateRandomNumberInRange(OCTET_LOWER_BOUND, OCTET_UPPER_BOUND);
        int thirdOctet = randomNumberGenerator.generateRandomNumberInRange(OCTET_LOWER_BOUND, OCTET_UPPER_BOUND);
        int forthOctet = randomNumberGenerator.generateRandomNumberInRange(OCTET_LOWER_BOUND, OCTET_UPPER_BOUND);
        return "%s.%s.%s.%s".formatted(firstOctet, secondOctet, thirdOctet, forthOctet);
    }
}
