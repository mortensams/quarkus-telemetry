# Telemetry Aggregation Service Design Document

## Overview
The Telemetry Aggregation Service is a RESTful service built with Quarkus that processes and aggregates device telemetry data. It reads temperature measurements from devices and provides flexible time-based aggregation capabilities.

## Architecture

### Components

#### 1. Data Model
- **TelemetryRecord**: Represents individual telemetry readings
  - deviceId: String
  - timestamp: Instant
  - ambientTemperature: double
  - deviceTemperature: double

- **AggregatedTelemetry**: Represents aggregated statistics for a time period
  - deviceId: String
  - startTime: Instant
  - endTime: Instant
  - avgAmbientTemperature: double
  - minAmbientTemperature: double
  - maxAmbientTemperature: double
  - avgDeviceTemperature: double
  - minDeviceTemperature: double
  - maxDeviceTemperature: double
  - recordCount: long

#### 2. Core Components
- **TelemetryResource**: REST endpoint handling HTTP requests
- **TelemetryService**: Business logic for data processing and aggregation
- **CSV Data Store**: File-based storage of raw telemetry data

### Data Flow
1. Client requests aggregated data with time range and resolution
2. TelemetryResource validates request parameters
3. TelemetryService loads relevant data from CSV
4. Data is filtered and aggregated based on time windows
5. Aggregated results are returned to client

## API Specification

### REST Endpoints

#### GET /telemetry/aggregate
Retrieves aggregated telemetry data for a specified time range and resolution.

**Query Parameters:**
- from: ISO-8601 timestamp (required)
- to: ISO-8601 timestamp (required)
- resolution: Time bucket size (required)

**Supported Resolutions:**
- 10s: 10 seconds
- 30s: 30 seconds
- 1m: 1 minute
- 5m: 5 minutes
- 15m: 15 minutes
- 30m: 30 minutes
- 1h: 1 hour
- 6h: 6 hours
- 12h: 12 hours
- 1d: 1 day

**Response Format:**
```json
[
  {
    "deviceId": "string",
    "startTime": "string (ISO-8601)",
    "endTime": "string (ISO-8601)",
    "avgAmbientTemperature": 0.0,
    "minAmbientTemperature": 0.0,
    "maxAmbientTemperature": 0.0,
    "avgDeviceTemperature": 0.0,
    "minDeviceTemperature": 0.0,
    "maxDeviceTemperature": 0.0,
    "recordCount": 0
  }
]
```

## Implementation Details

### Data Processing
- Raw data is stored in CSV format
- Data is loaded in chunks to manage memory efficiently
- Time-based filtering is applied during initial data load
- Records are grouped by device ID
- Statistics are calculated for each time bucket

### Aggregation Logic
1. Records are first grouped by device ID
2. For each device:
   - Records are sorted by timestamp
   - Time range is divided into buckets based on resolution
   - Records are assigned to appropriate buckets
   - Statistics are calculated for each bucket

### Error Handling
- Invalid time ranges return 400 Bad Request
- Unsupported resolutions return 400 Bad Request
- CSV file access errors return 500 Internal Server Error
- All errors include descriptive messages

## Performance Considerations
- CSV reading is optimized using OpenCSV
- Data is filtered early to reduce memory usage
- Stream operations are used for efficient data processing
- Time bucket calculations are optimized

## Testing
- Unit tests for aggregation logic
- Integration tests for API endpoints
- Performance tests for large datasets
- Edge case testing for time ranges and resolutions

## Future Improvements
1. Add support for multiple data sources
2. Implement caching for frequently requested aggregations
3. Add support for custom aggregation functions
4. Implement data persistence using a database
5. Add real-time data ingestion capabilities
