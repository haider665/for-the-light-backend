package For.the.light.iot.service;

import For.the.light.iot.entity.DeviceLiveStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class IotStatusService {

    private final Duration onlineThreshold;
    private final Duration offlineThreshold;

    public IotStatusService(
            @Value("${app.iot.status.online-seconds:60}") long onlineSeconds,
            @Value("${app.iot.status.offline-seconds:300}") long offlineSeconds) {
        this.onlineThreshold = Duration.ofSeconds(onlineSeconds);
        this.offlineThreshold = Duration.ofSeconds(offlineSeconds);
    }

    public DeviceLiveStatus resolve(Instant lastSeen, Instant now) {
        if (lastSeen == null) {
            return DeviceLiveStatus.OFFLINE;
        }

        Duration age = Duration.between(lastSeen, now);
        if (age.compareTo(onlineThreshold) < 0) {
            return DeviceLiveStatus.ONLINE;
        }

        if (age.compareTo(offlineThreshold) <= 0) {
            return DeviceLiveStatus.DELAYED;
        }

        return DeviceLiveStatus.OFFLINE;
    }
}
