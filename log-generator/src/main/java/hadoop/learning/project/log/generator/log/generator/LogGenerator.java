package hadoop.learning.project.log.generator.log.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogGenerator {

    private final IpAddressGenerator ipAddressGenerator;

    private final UserIdGenerator userIdGenerator;

    private final TimestampGenerator timestampGenerator;

    private final ResourceGenerator resourceGenerator;

    private final StatusCodeGenerator statusCodeGenerator;

    private final ResponseSizeGenerator responseSizeGenerator;

    /**
     * <ip-address> <userid> [<timestamp>] "<request>" <status-code> <response-size>
     * 192.168.1.15 - [24/Oct/2024:14:05:12] "POST /api/v1/login HTTP/1.1" 401 512
     */
    public String generateLog() {
        final String ipAddress = ipAddressGenerator.generateIpAddress();
        final String userId = userIdGenerator.generateUserId();
        final String timestamp = timestampGenerator.generateTimestamp();
        final String resource = resourceGenerator.generateResource();
        final int statusCode = statusCodeGenerator.generateStatusCode();
        final int responseSize = responseSizeGenerator.generateResponseSize(resource, statusCode);
        return "%s %s [%s] \"%s\" %s, %s".formatted(ipAddress, userId, timestamp, resource, statusCode, responseSize);
    }
}
