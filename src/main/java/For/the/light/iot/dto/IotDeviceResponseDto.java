package For.the.light.iot.dto;

import For.the.light.iot.entity.DeviceLiveStatus;
import lombok.Data;

import java.time.Instant;

@Data
public class IotDeviceResponseDto {
    private String deviceId;
    private String name;
    private Double latitude;
    private Double longitude;
    private Instant lastSeen;
    private Instant lastDeviceTimestamp;
    private DeviceLiveStatus status;
}
