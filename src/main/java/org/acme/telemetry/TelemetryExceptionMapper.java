package org.acme.telemetry;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class TelemetryExceptionMapper implements ExceptionMapper<Exception> {
    
    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof BadRequestException) {
            return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(exception.getMessage(), "INVALID_PARAMETERS"))
                .build();
        }
        
        // Handle parsing errors
        if (exception instanceof IllegalArgumentException) {
            return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("Invalid date format or parameters", "INVALID_FORMAT"))
                .build();
        }
        
        // Handle other runtime errors
        return Response
            .status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(new ErrorResponse("An internal error occurred", "INTERNAL_ERROR"))
            .build();
    }
}