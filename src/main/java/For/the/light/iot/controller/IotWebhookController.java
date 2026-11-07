package For.the.light.iot.controller;

import For.the.light.iot.dto.GpsWebhookRequestDto;
import For.the.light.iot.dto.IotWebhookAckDto;
import For.the.light.iot.security.RateLimitExceededException;
import For.the.light.iot.service.IotIngestionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/iot/webhook")
public class IotWebhookController {

    private final IotIngestionService iotIngestionService;

    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping() {
        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "service", "iot-webhook",
                "timestamp", java.time.Instant.now().toString()
        ));
    }

    @PostMapping("/gps")
    public ResponseEntity<?> ingestGps(
            @Valid @RequestBody GpsWebhookRequestDto request,
            HttpServletRequest httpServletRequest,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        String clientIp = extractClientIp(httpServletRequest);
        log.info("[Webhook] GPS request from ip={} | body={}", clientIp, request);
        try {
            IotWebhookAckDto response = iotIngestionService.ingest(request, authorization, clientIp);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } catch (RateLimitExceededException ex) {
            log.warn("IoT webhook rate limited for device {} from ip {}", request.getDeviceId(), clientIp);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error", ex.getMessage()));
        } catch (SecurityException ex) {
            log.warn("IoT webhook unauthorized for device {}: {}", request.getDeviceId(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            log.warn("IoT webhook rejected for device {}: {}", request.getDeviceId(), ex.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
