package For.the.light.iot.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class IotWebhookAuditService {

    public void logFailure(String outcome, String deviceId, String clientIp, String reason) {
        log.warn("iot_webhook_audit outcome={} deviceId={} clientIp={} reason={}", outcome, deviceId, clientIp, reason);
    }
}
