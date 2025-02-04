package org.acme.telemetry;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * REST endpoint for accessing device telemetry data.
 * Provides APIs to retrieve and aggregate temperature readings from devices.
 * 
 * Example usage:
 * GET /telemetry/aggregate?from=2024-02-02T00:00:00Z&to=2024-02-03T00:00:00Z&resolution=1h
 */
@Path("/telemetry")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Telemetry", description = "Operations for device telemetry data aggregation")
public class TelemetryResource {

    @Inject
    TelemetryService telemetryService;
    
    @Inject
    TelemetryValidator validator;

    /**
     * Retrieves aggregated telemetry data for a specified time range and resolution.
     * 
     * This endpoint aggregates temperature readings into time buckets and calculates
     * statistics (min, max, average) for each bucket. The data can be aggregated at
     * different resolutions, from 10 seconds to 1 day.
     * 
     * Example Request:
     * GET /telemetry/aggregate?from=2024-02-02T00:00:00Z&to=2024-02-03T00:00:00Z&resolution=1h
     * 
     * @param fromTime Start time in ISO-8601 format (e.g., "2024-02-02T00:00:00Z")
     * @param toTime End time in ISO-8601 format
     * @param resolution Time bucket size (e.g., "10s", "1m", "1h", "1d")
     * @return Response containing list of aggregated statistics or error details
     */
    @GET
    @Path("/aggregate")
    @Operation(
        summary = "Aggregate telemetry data",
        description = "Retrieves aggregated temperature data for devices within a specified time range and resolution"
    )
    @APIResponses(value = {
        @APIResponse(
            responseCode = "200",
            description = "Successfully retrieved aggregated data",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = AggregatedTelemetry.class))
        ),
        @APIResponse(
            responseCode = "400",
            description = "Invalid parameters provided",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))
        ),
        @APIResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public Response aggregateTelemetry(
            @Parameter(description = "Start time (ISO-8601 format)", 
                      example = "2024-02-02T00:00:00Z",
                      required = true)
            @QueryParam("from") String fromTime,
            
            @Parameter(description = "End time (ISO-8601 format)", 
                      example = "2024-02-03T00:00:00Z",
                      required = true)
            @QueryParam("to") String toTime,
            
            @Parameter(description = "Time resolution (10s, 30s, 1m, 5m, 15m, 30m, 1h, 6h, 12h, 1d)", 
                      example = "1h",
                      required = true)
            @QueryParam("resolution") String resolution) {
        
        try {
            // Step 1: Validate parameter presence
            if (fromTime == null || toTime == null || resolution == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Missing required parameters", "MISSING_PARAMETERS"))
                    .build();
            }

            // Step 2: Parse and validate timestamps
            Instant from = Instant.parse(fromTime);
            Instant to = Instant.parse(toTime);
            validator.validateTimeRange(from, to);
            
            // Step 3: Validate and parse resolution
            validator.validateResolution(resolution);
            Duration resolutionDuration = parseDuration(resolution);
            
            // Step 4: Retrieve aggregated data
            List<AggregatedTelemetry> result = telemetryService.aggregateTelemetry(from, to, resolutionDuration);
            return Response.ok(result).build();
            
        } catch (DateTimeParseException e) {
            // Handle invalid date format
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(
                    "Invalid date format. Use ISO-8601 format (e.g., 2024-02-02T00:00:00Z)", 
                    "INVALID_DATE_FORMAT"))
                .build();
        } catch (BadRequestException e) {
            // Handle validation errors
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage(), "INVALID_PARAMETERS"))
                .build();
        } catch (Exception e) {
            // Handle unexpected errors
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Internal server error", "INTERNAL_ERROR"))
                .build();
        }
    }

    /**
     * Converts a string resolution value to a Duration object.
     * 
     * @param resolution String value like "10s", "1m", "1h", "1d"
     * @return Corresponding Duration object
     * @throws BadRequestException if resolution format is invalid
     */
    private Duration parseDuration(String resolution) {
        return switch (resolution.toLowerCase()) {
            case "10s" -> Duration.ofSeconds(10);
            case "30s" -> Duration.ofSeconds(30);
            case "1m" -> Duration.ofMinutes(1);
            case "5m" -> Duration.ofMinutes(5);
            case "15m" -> Duration.ofMinutes(15);
            case "30m" -> Duration.ofMinutes(30);
            case "1h" -> Duration.ofHours(1);
            case "6h" -> Duration.ofHours(6);
            case "12h" -> Duration.ofHours(12);
            case "1d" -> Duration.ofDays(1);
            default -> throw new BadRequestException("Unsupported resolution: " + resolution);
        };
    }
}