package For.the.light.iot.service;

import For.the.light.iot.dto.CreateIotDeviceRequestDto;
import For.the.light.iot.dto.IotDeviceLocationPageDto;
import For.the.light.iot.dto.IotDeviceLocationResponseDto;
import For.the.light.iot.dto.IotDeviceResponseDto;
import For.the.light.iot.entity.IotDevice;
import For.the.light.iot.entity.IotDeviceLocation;
import For.the.light.iot.repository.IotDeviceLocationRepository;
import For.the.light.iot.repository.IotDeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IotDeviceService {

    private final IotDeviceRepository iotDeviceRepository;
    private final IotDeviceLocationRepository iotDeviceLocationRepository;
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

    @Transactional(readOnly = true)
    public IotDeviceLocationPageDto getDeviceLocations(
            String deviceId,
            Instant from,
            Instant to,
            int page,
            int size) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("from and to are required ISO-8601 timestamps");
        }

        if (to.isBefore(from)) {
            throw new IllegalArgumentException("to must be after from");
        }

        if (Duration.between(from, to).toDays() > 31) {
            throw new IllegalArgumentException("Maximum range is 31 days");
        }

        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 500);

        IotDevice device = iotDeviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found"));

        Pageable pageable = PageRequest.of(safePage, safeSize);
        Page<IotDeviceLocation> resultPage = iotDeviceLocationRepository
                .findByDeviceAndDeviceTimestampBetweenOrderByDeviceTimestampDesc(device, from, to, pageable);

        IotDeviceLocationPageDto dto = new IotDeviceLocationPageDto();
        dto.setDeviceId(deviceId);
        dto.setPage(resultPage.getNumber());
        dto.setSize(resultPage.getSize());
        dto.setTotalElements(resultPage.getTotalElements());
        dto.setTotalPages(resultPage.getTotalPages());
        dto.setItems(resultPage.getContent().stream().map(this::toLocationResponse).toList());

        return dto;
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
        dto.setEmergency(device.getLastEmergency());
        return dto;
    }

    private IotDeviceLocationResponseDto toLocationResponse(IotDeviceLocation location) {
        IotDeviceLocationResponseDto dto = new IotDeviceLocationResponseDto();
        dto.setLatitude(location.getLatitude());
        dto.setLongitude(location.getLongitude());
        dto.setDeviceTimestamp(location.getDeviceTimestamp());
        dto.setReceivedAt(location.getReceivedAt());
        dto.setEventId(location.getEventId());
        dto.setEmergency(location.getEmergency());
        return dto;
    }
}
