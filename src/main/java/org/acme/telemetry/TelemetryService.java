package org.acme.telemetry;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.InputStreamReader;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service responsible for processing and aggregating device telemetry data.
 * This service reads temperature data from a CSV file and provides methods to:
 * - Load telemetry data for a specific time range
 * - Aggregate data into time buckets of specified duration
 * - Calculate statistical measures (min, max, average) for each time bucket
 */
@ApplicationScoped
public class TelemetryService {
    
    /**
     * Path to the CSV file containing telemetry data.
     * Can be configured via application.properties using the key 'org.acme.telemetry.csv.path'
     */
    @ConfigProperty(name = "org.acme.telemetry.csv.path", defaultValue = "telemetry.csv")
    String csvFilePath;

    /**
     * Aggregates telemetry data for a specified time range and resolution.
     * 
     * @param fromTime Start of the time range (inclusive)
     * @param toTime End of the time range (exclusive)
     * @param resolution Duration of each time bucket (e.g., 1 minute, 1 hour)
     * @return List of aggregated telemetry data for each time bucket
     */
    public List<AggregatedTelemetry> aggregateTelemetry(Instant fromTime, Instant toTime, Duration resolution) {
        List<TelemetryRecord> records = loadTelemetryData(fromTime, toTime);
        return aggregateRecords(records, resolution);
    }

    /**
     * Loads telemetry data from the CSV file for the specified time range.
     * 
     * @param fromTime Start time for data loading
     * @param toTime End time for data loading
     * @return List of individual telemetry records within the time range
     * @throws RuntimeException if there's an error reading or parsing the CSV file
     */
    private List<TelemetryRecord> loadTelemetryData(Instant fromTime, Instant toTime) {
        List<TelemetryRecord> records = new ArrayList<>();

        // Use ClassLoader to read file from resources directory
        try (InputStreamReader isr = new InputStreamReader(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(csvFilePath));
             CSVReader reader = new CSVReader(isr)) {
                
            // Skip the header row (DeviceId,Timestamp,AmbientTemperature,DeviceTemperature)
            reader.readNext();

            String[] line;
            while ((line = reader.readNext()) != null) {
                // Parse timestamp from ISO-8601 format
                Instant timestamp = Instant.parse(line[1]);
                
                // Performance optimization: Skip records outside the requested time range
                if (timestamp.isBefore(fromTime) || timestamp.isAfter(toTime)) {
                    continue;
                }

                // Create a record from the CSV line
                TelemetryRecord record = new TelemetryRecord(
                    line[0], // deviceId
                    timestamp,
                    Double.parseDouble(line[2]), // ambientTemperature
                    Double.parseDouble(line[3])  // deviceTemperature
                );
                records.add(record);
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException("Error reading telemetry data from " + csvFilePath, e);
        }

        return records;
    }

    /**
     * Aggregates individual telemetry records into time buckets.
     * The process involves:
     * 1. Grouping records by device ID
     * 2. For each device, creating time buckets based on the resolution
     * 3. Calculating statistics for each bucket
     * 
     * @param records List of individual telemetry records to aggregate
     * @param resolution Duration of each time bucket
     * @return List of aggregated statistics for each time bucket
     */
    private List<AggregatedTelemetry> aggregateRecords(List<TelemetryRecord> records, Duration resolution) {
        // Step 1: Group records by device ID for separate processing of each device
        Map<String, List<TelemetryRecord>> deviceGroups = new HashMap<>();
        for (TelemetryRecord record : records) {
            deviceGroups.computeIfAbsent(record.getDeviceId(), k -> new ArrayList<>()).add(record);
        }

        List<AggregatedTelemetry> aggregatedResults = new ArrayList<>();

        // Step 2: Process each device's records separately
        for (Map.Entry<String, List<TelemetryRecord>> entry : deviceGroups.entrySet()) {
            String deviceId = entry.getKey();
            List<TelemetryRecord> deviceRecords = entry.getValue();

            // Sort records chronologically to ensure proper bucketing
            deviceRecords.sort((r1, r2) -> r1.getTimestamp().compareTo(r2.getTimestamp()));

            if (deviceRecords.isEmpty()) {
                continue;
            }

            // Find the time range for this device's data
            Instant startTime = deviceRecords.get(0).getTimestamp();
            Instant endTime = deviceRecords.get(deviceRecords.size() - 1).getTimestamp();

            // Step 3: Create time buckets and aggregate data
            for (Instant bucketStart = startTime; bucketStart.isBefore(endTime); bucketStart = bucketStart.plus(resolution)) {
                final Instant bucketStartFinal = bucketStart;
                final Instant bucketEnd = bucketStart.plus(resolution);
                
                // Filter records that fall within this time bucket
                List<TelemetryRecord> bucketRecords = deviceRecords.stream()
                    .filter(r -> !r.getTimestamp().isBefore(bucketStartFinal) && r.getTimestamp().isBefore(bucketEnd))
                    .toList();

                if (!bucketRecords.isEmpty()) {
                    // Create aggregation result for this bucket
                    AggregatedTelemetry aggregated = new AggregatedTelemetry();
                    aggregated.setDeviceId(deviceId);
                    aggregated.setStartTime(bucketStartFinal);
                    aggregated.setEndTime(bucketEnd);
                    aggregated.setRecordCount(bucketRecords.size());

                    // Extract temperature arrays for statistical calculations
                    double[] ambientTemps = bucketRecords.stream()
                        .mapToDouble(TelemetryRecord::getAmbientTemperature)
                        .toArray();
                    double[] deviceTemps = bucketRecords.stream()
                        .mapToDouble(TelemetryRecord::getDeviceTemperature)
                        .toArray();

                    // Calculate statistics for both temperature types
                    aggregated.setAvgAmbientTemperature(calculateAverage(ambientTemps));
                    aggregated.setMinAmbientTemperature(calculateMin(ambientTemps));
                    aggregated.setMaxAmbientTemperature(calculateMax(ambientTemps));
                    aggregated.setAvgDeviceTemperature(calculateAverage(deviceTemps));
                    aggregated.setMinDeviceTemperature(calculateMin(deviceTemps));
                    aggregated.setMaxDeviceTemperature(calculateMax(deviceTemps));

                    aggregatedResults.add(aggregated);
                }
            }
        }

        return aggregatedResults;
    }

    /**
     * Calculates the average value from an array of temperatures.
     * 
     * @param values Array of temperature values
     * @return Average temperature, or 0 if the array is empty
     */
    private double calculateAverage(double[] values) {
        if (values.length == 0) return 0;
        double sum = 0;
        for (double value : values) {
            sum += value;
        }
        return sum / values.length;
    }

    /**
     * Finds the minimum value in an array of temperatures.
     * 
     * @param values Array of temperature values
     * @return Minimum temperature, or 0 if the array is empty
     */
    private double calculateMin(double[] values) {
        if (values.length == 0) return 0;
        double min = values[0];
        for (double value : values) {
            if (value < min) min = value;
        }
        return min;
    }

    /**
     * Finds the maximum value in an array of temperatures.
     * 
     * @param values Array of temperature values
     * @return Maximum temperature, or 0 if the array is empty
     */
    private double calculateMax(double[] values) {
        if (values.length == 0) return 0;
        double max = values[0];
        for (double value : values) {
            if (value > max) max = value;
        }
        return max;
    }
}