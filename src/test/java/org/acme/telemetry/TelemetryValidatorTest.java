package org.acme.telemetry;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class TelemetryValidatorTest {

    @Inject
    TelemetryValidator validator;

    @Test
    void validateTimeRange_ValidRange_NoException() {
        Instant from = Instant.parse("2024-02-02T00:00:00Z");
        Instant to = Instant.parse("2024-02-03T00:00:00Z");
        
        assertDoesNotThrow(() -> validator.validateTimeRange(from, to));
    }

    @Test
    void validateTimeRange_FromAfterTo_ThrowsException() {
        Instant from = Instant.parse("2024-02-03T00:00:00Z");
        Instant to = Instant.parse("2024-02-02T00:00:00Z");
        
        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> validator.validateTimeRange(from, to));
        assertEquals("'from' time must be before 'to' time", exception.getMessage());
    }

    @Test
    void validateTimeRange_RangeTooLarge_ThrowsException() {
        Instant from = Instant.parse("2024-02-02T00:00:00Z");
        Instant to = Instant.parse("2024-03-04T00:00:00Z"); // 31 days
        
        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> validator.validateTimeRange(from, to));
        assertEquals("Time range cannot exceed 30 days", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"10s", "30s", "1m", "5m", "15m", "30m", "1h", "6h", "12h", "1d"})
    void validateResolution_ValidResolutions_NoException(String resolution) {
        assertDoesNotThrow(() -> validator.validateResolution(resolution));
    }

    @Test
    void validateResolution_InvalidResolution_ThrowsException() {
        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> validator.validateResolution("2h"));
        assertTrue(exception.getMessage().contains("Invalid resolution"));
    }

    @Test
    void validateResolution_NullResolution_ThrowsException() {
        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> validator.validateResolution(null));
        assertEquals("Resolution parameter is required", exception.getMessage());
    }
}