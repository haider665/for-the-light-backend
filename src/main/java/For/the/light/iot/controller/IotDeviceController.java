package For.the.light.iot.controller;

import For.the.light.iot.dto.CreateIotDeviceRequestDto;
import For.the.light.iot.dto.IotDeviceLocationPageDto;
import For.the.light.iot.dto.IotDeviceResponseDto;
import For.the.light.iot.service.IotDeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/iot/devices")
@PreAuthorize("hasAuthority('SUPER_ADMIN')")
public class IotDeviceController {

    private final IotDeviceService iotDeviceService;

    @PostMapping
    public ResponseEntity<?> createDevice(@Valid @RequestBody CreateIotDeviceRequestDto request) {
        try {
            return ResponseEntity.ok(iotDeviceService.register(request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<IotDeviceResponseDto>> getDevices() {
        return ResponseEntity.ok(iotDeviceService.getAllDevices());
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<?> getDevice(@PathVariable String deviceId) {
        try {
            return ResponseEntity.ok(iotDeviceService.getDevice(deviceId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{deviceId}/locations")
    public ResponseEntity<?> getDeviceLocations(
            @PathVariable String deviceId,
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "200") int size) {
        try {
            Instant fromInstant = Instant.parse(from);
            Instant toInstant = Instant.parse(to);
            IotDeviceLocationPageDto response = iotDeviceService.getDeviceLocations(deviceId, fromInstant, toInstant, page, size);
            return ResponseEntity.ok(response);
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid from/to format. Expected ISO-8601"));
        } catch (IllegalArgumentException ex) {
            if ("Device not found".equals(ex.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
