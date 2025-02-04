package org.acme.telemetry;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import java.time.Instant;

/**
 * Validates input parameters for telemetry data requests.
 * This class ensures that all parameters meet the required criteria before processing.
 */
@ApplicationScoped
public class TelemetryValidator {
    
    /**
     * Maximum allowed time range for queries in days.
     * This limit helps prevent excessive resource usage from very large queries.
     */
    private static final int MAX_TIME_RANGE_DAYS = 30;
    
    /**
     * Validates the time range parameters for a telemetry request.
     * 
     * @param from Start time of the range
     * @param to End time of the range
     * @throws BadRequestException if:
     *         - Either parameter is null
     *         - Start time is after end time
     *         - Time range exceeds maximum allowed duration
     */
    public void validateTimeRange(Instant from, Instant to) {
        if (from == null || to == null) {
            throw new BadRequestException("Both 'from' and 'to' parameters are required");
        }
        
        if (from.isAfter(to)) {
            throw new BadRequestException("'from' time must be before 'to' time");
        }
        
        // Calculate duration in milliseconds and compare with max allowed
        long durationMillis = to.toEpochMilli() - from.toEpochMilli();
        long maxMillis = MAX_TIME_RANGE_DAYS * 24L * 60L * 60L * 1000L;
        if (durationMillis > maxMillis) {
            throw new BadRequestException("Time range cannot exceed " + MAX_TIME_RANGE_DAYS + " days");
        }
    }
    
    /**
     * Validates the resolution parameter for data aggregation.
     * 
     * @param resolution Time bucket size (e.g., "10s", "1h", "1d")
     * @throws BadRequestException if:
     *         - Resolution is null or empty
     *         - Resolution format is invalid
     *         - Resolution value is not supported
     */
    public void validateResolution(String resolution) {
        if (resolution == null || resolution.trim().isEmpty()) {
            throw new BadRequestException("Resolution parameter is required");
        }
        
        // List of all supported resolution values
        String[] validResolutions = {
            "10s", // 10 seconds
            "30s", // 30 seconds
            "1m",  // 1 minute
            "5m",  // 5 minutes
            "15m", // 15 minutes
            "30m", // 30 minutes
            "1h",  // 1 hour
            "6h",  // 6 hours
            "12h", // 12 hours
            "1d"   // 1 day
        };
        
        // Check if the provided resolution is in the list of valid values
        boolean isValid = false;
        for (String validRes : validResolutions) {
            if (validRes.equals(resolution.toLowerCase())) {
                isValid = true;
                break;
            }
        }
        
        if (!isValid) {
            throw new BadRequestException(
                "Invalid resolution. Supported values: 10s, 30s, 1m, 5m, 15m, 30m, 1h, 6h, 12h, 1d");
        }
    }
}