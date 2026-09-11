package For.the.light.iot.dto;

import For.the.light.iot.entity.DeviceLiveStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class IotWebhookAckDto {
    private boolean accepted;
    private boolean duplicate;
    private String deviceId;
    private Instant deviceTimestamp;
    private Instant receivedAt;
    private DeviceLiveStatus status;
    private String message;
}
