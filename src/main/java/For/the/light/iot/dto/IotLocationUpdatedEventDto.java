package For.the.light.iot.dto;

import For.the.light.iot.entity.DeviceLiveStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class IotLocationUpdatedEventDto {
    private String type;
    private String deviceId;
    private String name;
    private Double latitude;
    private Double longitude;
    private Instant timestamp;
    private Instant lastSeen;
    private DeviceLiveStatus status;
}
