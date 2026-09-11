package For.the.light.iot.controller;

import For.the.light.iot.dto.GpsWebhookRequestDto;
import For.the.light.iot.dto.IotWebhookAckDto;
import For.the.light.iot.service.IotIngestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/gps")
    public ResponseEntity<?> ingestGps(
            @Valid @RequestBody GpsWebhookRequestDto request,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            IotWebhookAckDto response = iotIngestionService.ingest(request, authorization);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } catch (SecurityException ex) {
            log.warn("IoT webhook unauthorized for device {}: {}", request.getDeviceId(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            log.warn("IoT webhook rejected for device {}: {}", request.getDeviceId(), ex.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
