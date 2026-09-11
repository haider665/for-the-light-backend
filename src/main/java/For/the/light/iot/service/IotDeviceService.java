package For.the.light.iot.service;

import For.the.light.iot.dto.CreateIotDeviceRequestDto;
import For.the.light.iot.dto.IotDeviceResponseDto;
import For.the.light.iot.entity.IotDevice;
import For.the.light.iot.repository.IotDeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IotDeviceService {

    private final IotDeviceRepository iotDeviceRepository;
    private final IotStatusService iotStatusService;

    @Transactional
    public IotDeviceResponseDto register(CreateIotDeviceRequestDto request) {
        iotDeviceRepository.findByDeviceId(request.getDeviceId()).ifPresent(existing -> {
            throw new IllegalArgumentException("Device already exists");
        });

        IotDevice device = new IotDevice();
        device.setDeviceId(request.getDeviceId());
        device.setName(request.getName());
        device.setApiKeyHash(BCrypt.hashpw(request.getApiKey(), BCrypt.gensalt()));

        IotDevice saved = iotDeviceRepository.save(device);
        return toResponse(saved, Instant.now());
    }

    @Transactional(readOnly = true)
    public List<IotDeviceResponseDto> getAllDevices() {
        Instant now = Instant.now();
        return iotDeviceRepository.findAllByOrderByCreatedDesc()
                .stream()
                .map(device -> toResponse(device, now))
                .toList();
    }

    @Transactional(readOnly = true)
    public IotDeviceResponseDto getDevice(String deviceId) {
        IotDevice device = iotDeviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found"));
        return toResponse(device, Instant.now());
    }

    private IotDeviceResponseDto toResponse(IotDevice device, Instant now) {
        IotDeviceResponseDto dto = new IotDeviceResponseDto();
        dto.setDeviceId(device.getDeviceId());
        dto.setName(device.getName());
        dto.setLatitude(device.getLastLatitude());
        dto.setLongitude(device.getLastLongitude());
        dto.setLastSeen(device.getLastSeen());
        dto.setLastDeviceTimestamp(device.getLastDeviceTimestamp());
        dto.setStatus(iotStatusService.resolve(device.getLastSeen(), now));
        return dto;
    }
}
