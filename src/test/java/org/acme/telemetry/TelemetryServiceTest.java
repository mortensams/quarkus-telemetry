package org.acme.telemetry;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class TelemetryServiceTest {

    @Inject
    TelemetryService telemetryService;

    private Instant startTime;
    private Instant endTime;

    @BeforeEach
    void setUp() {
        startTime = Instant.parse("2024-02-02T00:00:00Z");
        endTime = Instant.parse("2024-02-02T00:01:00Z");
    }

    @Test
    void aggregateTelemetry_30SecondResolution_CorrectAggregation() {
        List<AggregatedTelemetry> result = telemetryService.aggregateTelemetry(
            startTime, endTime, Duration.ofSeconds(30));

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2); // Should have two 30-second buckets

        AggregatedTelemetry firstBucket = result.get(0);
        assertThat(firstBucket.getStartTime()).isEqualTo(startTime);
        assertThat(firstBucket.getEndTime()).isEqualTo(startTime.plusSeconds(30));
        assertThat(firstBucket.getRecordCount()).isEqualTo(3);
        assertThat(firstBucket.getAvgAmbientTemperature()).isBetween(20.0, 21.0);
        assertThat(firstBucket.getAvgDeviceTemperature()).isBetween(50.0, 52.0);
    }

    @Test
    void aggregateTelemetry_FullMinute_CorrectAggregation() {
        List<AggregatedTelemetry> result = telemetryService.aggregateTelemetry(
            startTime, endTime, Duration.ofMinutes(1));

        assertThat(result).hasSize(1); // Should have one 1-minute bucket
        
        AggregatedTelemetry bucket = result.get(0);
        assertThat(bucket.getStartTime()).isEqualTo(startTime);
        assertThat(bucket.getEndTime()).isEqualTo(endTime);
        assertThat(bucket.getRecordCount()).isEqualTo(6);
        
        // Verify min/max values
        assertThat(bucket.getMinAmbientTemperature()).isEqualTo(20.4);
        assertThat(bucket.getMaxAmbientTemperature()).isEqualTo(20.9);
        assertThat(bucket.getMinDeviceTemperature()).isEqualTo(50.5);
        assertThat(bucket.getMaxDeviceTemperature()).isEqualTo(51.75);
    }
}