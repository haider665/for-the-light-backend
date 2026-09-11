package For.the.light.iot.service;

import For.the.light.iot.repository.IotDeviceLocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class IotRetentionService {

    private final IotDeviceLocationRepository iotDeviceLocationRepository;

    @Value("${app.iot.retention.days:90}")
    private long retentionDays;

    @Scheduled(cron = "${app.iot.retention.cron:0 0 3 * * *}")
    @Transactional
    public void purgeOldLocations() {
        Instant cutoff = Instant.now().minus(retentionDays, ChronoUnit.DAYS);
        long deleted = iotDeviceLocationRepository.deleteByReceivedAtBefore(cutoff);
        if (deleted > 0) {
            log.info("iot_retention_cleanup deleted={} cutoff={}", deleted, cutoff);
        }
    }
}
