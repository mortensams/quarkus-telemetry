package org.acme.telemetry;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Represents an error response returned by the API.
 * This class provides structured error information including a human-readable message
 * and a machine-readable error code.
 */
@Schema(name = "ErrorResponse", description = "Error details returned when a request fails")
public class ErrorResponse {
    
    /**
     * Human-readable error message explaining what went wrong
     */
    @Schema(description = "Human-readable error message", 
            example = "Invalid time range provided: 'from' date must be before 'to' date")
    private String message;
    
    /**
     * Machine-readable error code for programmatic error handling
     * Common codes include:
     * - INVALID_PARAMETERS: Request parameters are invalid
     * - INVALID_DATE_FORMAT: Date format is incorrect
     * - MISSING_PARAMETERS: Required parameters are missing
     * - INTERNAL_ERROR: Unexpected server error
     */
    @Schema(description = "Machine-readable error code", 
            example = "INVALID_PARAMETERS",
            enumeration = {"INVALID_PARAMETERS", "INVALID_DATE_FORMAT", "MISSING_PARAMETERS", "INTERNAL_ERROR"})
    private String code;
    
    /**
     * Creates a new error response with the specified message and code.
     *
     * @param message Human-readable error message
     * @param code Machine-readable error code
     */
    public ErrorResponse(String message, String code) {
        this.message = message;
        this.code = code;
    }
    
    /** @return The error message */
    public String getMessage() {
        return message;
    }
    
    /** @param message The error message to set */
    public void setMessage(String message) {
        this.message = message;
    }
    
    /** @return The error code */
    public String getCode() {
        return code;
    }
    
    /** @param code The error code to set */
    public void setCode(String code) {
        this.code = code;
    }
}