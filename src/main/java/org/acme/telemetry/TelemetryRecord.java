package org.acme.telemetry;

import java.time.Instant;

/**
 * Represents a single telemetry reading from a device.
 * This is the raw data model that corresponds to one line in the CSV file.
 * Each record contains temperature readings at a specific point in time.
 */
public class TelemetryRecord {
    /** Unique identifier of the device that generated this reading */
    private String deviceId;
    
    /** Exact time when this reading was taken (in UTC) */
    private Instant timestamp;
    
    /** Ambient temperature around the device in Celsius */
    private double ambientTemperature;
    
    /** Internal temperature of the device in Celsius */
    private double deviceTemperature;

    /**
     * Default constructor required for JSON serialization/deserialization
     */
    public TelemetryRecord() {}

    /**
     * Creates a new telemetry record with all required fields.
     *
     * @param deviceId Identifier of the device
     * @param timestamp Time of the reading (UTC)
     * @param ambientTemperature Ambient temperature in Celsius
     * @param deviceTemperature Device temperature in Celsius
     */
    public TelemetryRecord(String deviceId, Instant timestamp, double ambientTemperature, double deviceTemperature) {
        this.deviceId = deviceId;
        this.timestamp = timestamp;
        this.ambientTemperature = ambientTemperature;
        this.deviceTemperature = deviceTemperature;
    }

    // Getters and setters with property documentation
    
    /** @return The device identifier */
    public String getDeviceId() {
        return deviceId;
    }

    /** @param deviceId The device identifier to set */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /** @return The timestamp of the reading in UTC */
    public Instant getTimestamp() {
        return timestamp;
    }

    /** @param timestamp The UTC timestamp to set */
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    /** @return The ambient temperature in Celsius */
    public double getAmbientTemperature() {
        return ambientTemperature;
    }

    /** @param ambientTemperature The ambient temperature to set in Celsius */
    public void setAmbientTemperature(double ambientTemperature) {
        this.ambientTemperature = ambientTemperature;
    }

    /** @return The device temperature in Celsius */
    public double getDeviceTemperature() {
        return deviceTemperature;
    }

    /** @param deviceTemperature The device temperature to set in Celsius */
    public void setDeviceTemperature(double deviceTemperature) {
        this.deviceTemperature = deviceTemperature;
    }
}