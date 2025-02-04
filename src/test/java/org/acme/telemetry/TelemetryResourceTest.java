package org.acme.telemetry;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.hamcrest.Matchers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
public class TelemetryResourceTest {

    @Test
    void testAggregateEndpoint_ValidRequest_Returns200() {
        given()
            .contentType(ContentType.JSON)
            .queryParam("from", "2024-02-02T00:00:00Z")
            .queryParam("to", "2024-02-02T00:01:00Z")
            .queryParam("resolution", "30s")
        .when()
            .get("/telemetry/aggregate")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("size()", is(2))
            .body("[0].deviceId", equalTo("DEVICE_001"))
            .body("[0].recordCount", Matchers.not(equalTo(0)))
            .body("[0].avgAmbientTemperature", notNullValue())
            .body("[0].avgDeviceTemperature", notNullValue());
    }

    @Test
    void testAggregateEndpoint_InvalidTimeRange_Returns400() {
        given()
            .contentType(ContentType.JSON)
            .queryParam("from", "2024-02-03T00:00:00Z")  // from after to
            .queryParam("to", "2024-02-02T00:00:00Z")
            .queryParam("resolution", "1h")
        .when()
            .get("/telemetry/aggregate")
        .then()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("message", containsString("'from' time must be before 'to' time"));
    }

    @Test
    void testAggregateEndpoint_InvalidResolution_Returns400() {
        given()
            .contentType(ContentType.JSON)
            .queryParam("from", "2024-02-02T00:00:00Z")
            .queryParam("to", "2024-02-03T00:00:00Z")
            .queryParam("resolution", "2h")  // invalid resolution
        .when()
            .get("/telemetry/aggregate")
        .then()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("message", containsString("Invalid resolution"));
    }

    @Test
    void testAggregateEndpoint_MissingParameters_Returns400() {
        given()
            .contentType(ContentType.JSON)
            .queryParam("from", "2024-02-02T00:00:00Z")
            // missing 'to' parameter
            .queryParam("resolution", "1h")
        .when()
            .get("/telemetry/aggregate")
        .then()
            .statusCode(400);
    }

    @Test
    void testAggregateEndpoint_InvalidDateFormat_Returns400() {
        given()
            .contentType(ContentType.JSON)
            .queryParam("from", "2024-02-02")  // invalid date format
            .queryParam("to", "2024-02-03T00:00:00Z")
            .queryParam("resolution", "1h")
        .when()
            .get("/telemetry/aggregate")
        .then()
            .statusCode(400);
    }
}