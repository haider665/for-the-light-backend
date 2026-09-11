package For.the.light.iot.repository;

import For.the.light.iot.entity.IotDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IotDeviceRepository extends JpaRepository<IotDevice, Long> {
    Optional<IotDevice> findByDeviceId(String deviceId);
}
