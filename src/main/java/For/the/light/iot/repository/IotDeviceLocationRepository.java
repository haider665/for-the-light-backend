package For.the.light.iot.repository;

import For.the.light.iot.entity.IotDevice;
import For.the.light.iot.entity.IotDeviceLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface IotDeviceLocationRepository extends JpaRepository<IotDeviceLocation, Long> {
    Page<IotDeviceLocation> findByDeviceAndDeviceTimestampBetweenOrderByDeviceTimestampDesc(
            IotDevice device,
            Instant from,
            Instant to,
            Pageable pageable);

    Optional<IotDeviceLocation> findByDeviceAndEventId(IotDevice device, String eventId);
}
