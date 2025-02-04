package org.acme.telemetry;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import java.time.Instant;

/**
 * Represents aggregated telemetry statistics for a specific time period.
 * This class contains statistical data (min, max, average) calculated from
 * multiple individual telemetry readings within a time bucket.
 */
@Schema(name = "AggregatedTelemetry", description = "Aggregated telemetry statistics for a time period")
public class AggregatedTelemetry {
    
    @Schema(description = "Device identifier", example = "DEVICE_001")
    private String deviceId;

    @Schema(description = "Start time of the aggregation period (inclusive)", example = "2024-02-02T00:00:00Z")
    private Instant startTime;

    @Schema(description = "End time of the aggregation period (exclusive)", example = "2024-02-02T01:00:00Z")
    private Instant endTime;

    @Schema(description = "Average ambient temperature in Celsius during the period", example = "22.5")
    private double avgAmbientTemperature;

    @Schema(description = "Lowest recorded ambient temperature in Celsius during the period", example = "18.2")
    private double minAmbientTemperature;

    @Schema(description = "Highest recorded ambient temperature in Celsius during the period", example = "25.8")
    private double maxAmbientTemperature;

    @Schema(description = "Average device temperature in Celsius during the period", example = "50.3")
    private double avgDeviceTemperature;

    @Schema(description = "Lowest recorded device temperature in Celsius during the period", example = "48.5")
    private double minDeviceTemperature;

    @Schema(description = "Highest recorded device temperature in Celsius during the period", example = "65.2")
    private double maxDeviceTemperature;

    @Schema(description = "Number of telemetry records included in this aggregation", example = "3600")
    private long recordCount;

    // Getters and setters with property documentation

    /** @return The device identifier */
    public String getDeviceId() {
        return deviceId;
    }

    /** @param deviceId The device identifier to set */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /** @return Start time of the aggregation period (inclusive) */
    public Instant getStartTime() {
        return startTime;
    }

    /** @param startTime Start time to set for the aggregation period */
    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    /** @return End time of the aggregation period (exclusive) */
    public Instant getEndTime() {
        return endTime;
    }

    /** @param endTime End time to set for the aggregation period */
    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    /** @return Average ambient temperature in Celsius */
    public double getAvgAmbientTemperature() {
        return avgAmbientTemperature;
    }

    /** @param avgAmbientTemperature Average ambient temperature to set in Celsius */
    public void setAvgAmbientTemperature(double avgAmbientTemperature) {
        this.avgAmbientTemperature = avgAmbientTemperature;
    }

    /** @return Minimum ambient temperature in Celsius */
    public double getMinAmbientTemperature() {
        return minAmbientTemperature;
    }

    /** @param minAmbientTemperature Minimum ambient temperature to set in Celsius */
    public void setMinAmbientTemperature(double minAmbientTemperature) {
        this.minAmbientTemperature = minAmbientTemperature;
    }

    /** @return Maximum ambient temperature in Celsius */
    public double getMaxAmbientTemperature() {
        return maxAmbientTemperature;
    }

    /** @param maxAmbientTemperature Maximum ambient temperature to set in Celsius */
    public void setMaxAmbientTemperature(double maxAmbientTemperature) {
        this.maxAmbientTemperature = maxAmbientTemperature;
    }

    /** @return Average device temperature in Celsius */
    public double getAvgDeviceTemperature() {
        return avgDeviceTemperature;
    }

    /** @param avgDeviceTemperature Average device temperature to set in Celsius */
    public void setAvgDeviceTemperature(double avgDeviceTemperature) {
        this.avgDeviceTemperature = avgDeviceTemperature;
    }

    /** @return Minimum device temperature in Celsius */
    public double getMinDeviceTemperature() {
        return minDeviceTemperature;
    }

    /** @param minDeviceTemperature Minimum device temperature to set in Celsius */
    public void setMinDeviceTemperature(double minDeviceTemperature) {
        this.minDeviceTemperature = minDeviceTemperature;
    }

    /** @return Maximum device temperature in Celsius */
    public double getMaxDeviceTemperature() {
        return maxDeviceTemperature;
    }

    /** @param maxDeviceTemperature Maximum device temperature to set in Celsius */
    public void setMaxDeviceTemperature(double maxDeviceTemperature) {
        this.maxDeviceTemperature = maxDeviceTemperature;
    }

    /** @return Number of records included in this aggregation */
    public long getRecordCount() {
        return recordCount;
    }

    /** @param recordCount Number of records to set */
    public void setRecordCount(long recordCount) {
        this.recordCount = recordCount;
    }
}