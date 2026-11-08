package For.the.light.iot.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class IotDeviceLocationResponseDto {
    private Double latitude;
    private Double longitude;
    private Instant deviceTimestamp;
    private Instant receivedAt;
    private String eventId;
    private String emergency;
}
