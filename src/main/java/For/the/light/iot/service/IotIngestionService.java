package For.the.light.iot.service;

import For.the.light.iot.dto.GpsWebhookRequestDto;
import For.the.light.iot.dto.IotWebhookAckDto;
import For.the.light.iot.entity.IotDevice;
import For.the.light.iot.entity.IotDeviceLocation;
import For.the.light.iot.repository.IotDeviceLocationRepository;
import For.the.light.iot.repository.IotDeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
public class IotIngestionService {

    private final IotDeviceRepository iotDeviceRepository;
    private final IotDeviceLocationRepository iotDeviceLocationRepository;
    private final IotStatusService iotStatusService;

    @Transactional
    public IotWebhookAckDto ingest(GpsWebhookRequestDto request, String authorizationHeader) {
        String apiKey = extractBearerToken(authorizationHeader);

        IotDevice device = iotDeviceRepository.findByDeviceId(request.getDeviceId())
                .orElseThrow(() -> new IllegalArgumentException("Unknown deviceId"));

        if (device.getApiKeyHash() == null || !isSecretMatch(apiKey, device.getApiKeyHash())) {
            throw new SecurityException("Invalid device credentials");
        }

        Instant deviceTimestamp = parseTimestamp(request.getTimestamp());
        Instant receivedAt = Instant.now();

        if (isDuplicate(request, device, deviceTimestamp)) {
            device.setLastSeen(receivedAt);
            iotDeviceRepository.save(device);

            return IotWebhookAckDto.builder()
                    .accepted(true)
                    .duplicate(true)
                    .deviceId(device.getDeviceId())
                    .deviceTimestamp(deviceTimestamp)
                    .receivedAt(receivedAt)
                    .status(iotStatusService.resolve(device.getLastSeen(), receivedAt))
                    .message("Duplicate event accepted")
                    .build();
        }

        IotDeviceLocation location = new IotDeviceLocation();
        location.setDevice(device);
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setDeviceTimestamp(deviceTimestamp);
        location.setReceivedAt(receivedAt);
        location.setEventId(request.getEventId());
        iotDeviceLocationRepository.save(location);

        device.setLastSeen(receivedAt);
        if (device.getLastDeviceTimestamp() == null || deviceTimestamp.isAfter(device.getLastDeviceTimestamp())) {
            device.setLastDeviceTimestamp(deviceTimestamp);
            device.setLastLatitude(request.getLatitude());
            device.setLastLongitude(request.getLongitude());
        }

        iotDeviceRepository.save(device);

        return IotWebhookAckDto.builder()
                .accepted(true)
                .duplicate(false)
                .deviceId(device.getDeviceId())
                .deviceTimestamp(deviceTimestamp)
                .receivedAt(receivedAt)
                .status(iotStatusService.resolve(device.getLastSeen(), receivedAt))
                .message("Location ingested")
                .build();
    }

    private boolean isDuplicate(GpsWebhookRequestDto request, IotDevice device, Instant deviceTimestamp) {
        if (request.getEventId() != null && !request.getEventId().isBlank()) {
            return iotDeviceLocationRepository.findByDeviceAndEventId(device, request.getEventId()).isPresent();
        }

        return iotDeviceLocationRepository.existsByDeviceAndDeviceTimestampAndLatitudeAndLongitude(
                device,
                deviceTimestamp,
                request.getLatitude(),
                request.getLongitude());
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new SecurityException("Missing bearer token");
        }

        String token = authorizationHeader.substring(7).trim();
        if (token.isBlank()) {
            throw new SecurityException("Missing bearer token");
        }

        return token;
    }

    private Instant parseTimestamp(String value) {
        try {
            return Instant.parse(value);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid timestamp format. Expected ISO-8601");
        }
    }

    private boolean isSecretMatch(String rawSecret, String hash) {
        try {
            return BCrypt.checkpw(rawSecret, hash);
        } catch (Exception ex) {
            return false;
        }
    }
}
