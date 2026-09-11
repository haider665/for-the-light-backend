package For.the.light.iot.controller;

import For.the.light.iot.dto.CreateIotDeviceRequestDto;
import For.the.light.iot.dto.IotDeviceResponseDto;
import For.the.light.iot.service.IotDeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
