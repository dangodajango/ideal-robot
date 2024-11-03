package hadoop.learning.project.log.processor.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public record LogLine(String ipAddress,
                      Optional<String> user,
                      LocalDateTime timestamp,
                      Resource resource,
                      int statusCode,
                      long responseSize) {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss");

    public static LogLine mapToLogLine(String logLineString) {
        String[] logLine = logLineString.split(" ");
        String ipAddress = logLine[0];
        Optional<String> user = logLine[1].equals("-") ? Optional.empty() : Optional.of(logLine[1]);
        LocalDateTime timestamp = parseTimestamp(logLine[2]);
        Resource resource = Resource.mapToResource(logLine[3]);
        int statusCode = Integer.parseInt(logLine[4]);
        long responseSize = Long.parseLong(logLine[5]);
        return new LogLine(ipAddress, user, timestamp, resource, statusCode, responseSize);
    }

    private static LocalDateTime parseTimestamp(String timestamp) {
        String cleanTimestamp = timestamp.substring(1, timestamp.length() - 1);
        return LocalDateTime.parse(cleanTimestamp, DATE_TIME_FORMATTER);
    }
}
