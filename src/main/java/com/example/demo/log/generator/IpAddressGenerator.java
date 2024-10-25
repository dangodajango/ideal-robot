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
        String generatedIpAddress = "%s.%s.%s.%s".formatted(firstOctet, secondOctet, thirdOctet, forthOctet);
        assert isIpAddressValid(generatedIpAddress) : "Generated IP address is not valid - %s".formatted(generatedIpAddress);
        return generatedIpAddress;
    }


    private boolean isIpAddressValid(String ipAddress) {
        String[] octets = ipAddress.split("\\.");
        if (octets.length != 4) {
            return false;
        }

        for (String octet : octets) {
            if (!isValidOctet(octet)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidOctet(String octet) {
        try {
            int octetInt = Integer.parseInt(octet);
            if (octetInt < 0 || octetInt > 255) {
                return false;
            }
        } catch (NumberFormatException numberFormatException) {
            return false;
        }
        return true;
    }
}
