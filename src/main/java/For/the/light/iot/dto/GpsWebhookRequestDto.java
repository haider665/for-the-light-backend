package For.the.light.iot.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GpsWebhookRequestDto {

    private String eventId;

    @NotBlank(message = "deviceId is required")
    @JsonProperty("device_id")
    @JsonAlias("deviceId")
    private String deviceId;

    @NotNull(message = "latitude is required")
    @DecimalMin(value = "-90.0", message = "latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "latitude must be <= 90")
    private Double latitude;

    @NotNull(message = "longitude is required")
    @DecimalMin(value = "-180.0", message = "longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "longitude must be <= 180")
    private Double longitude;

    // Optional — if not sent by device, server uses Instant.now()
    private String timestamp;

    // Extra telemetry fields — accepted for logging, not persisted to DB yet
    private String emergency;

    private Double altitude;

    @JsonProperty("speed_kmh")
    private Double speedKmh;

    private Integer satellites;

    @JsonProperty("battery_voltage")
    private Double batteryVoltage;

    @JsonProperty("battery_percent")
    private Integer batteryPercent;

    @JsonProperty("charge_status")
    private String chargeStatus;

    private String source;
}
