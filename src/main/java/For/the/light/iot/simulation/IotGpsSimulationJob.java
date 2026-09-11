package For.the.light.iot.simulation;

import For.the.light.iot.entity.IotDevice;
import For.the.light.iot.entity.IotDeviceLocation;
import For.the.light.iot.repository.IotDeviceLocationRepository;
import For.the.light.iot.repository.IotDeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simulates realistic GPS telemetry for fishing boats.
 * <p>
 * Origin: Open water in the Bay of Bengal, south of Mohipur Fish Landing Center.
 * The origin point (21.7850, 90.1200) is in the estuary/bay where the Andharmanik
 * River meets the Bay of Bengal — fully on water.
 * <p>
 * Boats move southward further into the Bay of Bengal (bearings 160°–200°).
 * All generated coordinates stay on open water.
 * <p>
 * Boat lifecycle per trip:
 *   DEPARTING → cruising at 6-8 knots out to fishing grounds (3-6 km further south)
 *   FISHING   → drifting slowly at 1-2 knots for 15-40 minutes
 *   RETURNING → heading back at 4-6 knots to the origin
 *   DOCKED    → idle for 5-15 minutes before next trip
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IotGpsSimulationJob {

    private final IotDeviceRepository deviceRepository;
    private final IotDeviceLocationRepository locationRepository;

    // ── Origin: Bay of Bengal, south of Mohipur ─────────────────
    // This point is in the open estuary/bay — confirmed on water.
    // Approx 8 km south of the Mohipur Fish Landing Center,
    // where the Andharmanik River opens into the Bay of Bengal.
    private static final double ORIGIN_LAT = 21.7850;
    private static final double ORIGIN_LNG = 90.1200;

    private final Map<String, BoatState> states = new ConcurrentHashMap<>();

    // Compass bearings — all point south into open Bay of Bengal
    private static final Map<String, Double> BEARINGS = Map.of(
            "BOAT-001", 175.0,
            "BOAT-002", 180.0,
            "BOAT-003", 185.0,
            "BOAT-004", 170.0,
            "BOAT-005", 190.0,
            "BOAT-006", 178.0,
            "BOAT-007", 183.0,
            "BOAT-008", 188.0,
            "BOAT-009", 172.0,
            "BOAT-010", 195.0
    );

    // ── Speed constants (km per minute) ─────────────────────────
    // 1 knot = 1.852 km/h = 0.03087 km/min
    private static final double KNOT_TO_KM_PER_MIN = 1.852 / 60.0;

    // Small fishing boat speeds (wooden trawler with outboard motor)
    private static final double CRUISE_MIN = 3.0 * KNOT_TO_KM_PER_MIN;  // ~0.093 km/min
    private static final double CRUISE_MAX = 4.0 * KNOT_TO_KM_PER_MIN;  // ~0.123 km/min
    private static final double DRIFT_MIN  = 0.2 * KNOT_TO_KM_PER_MIN;  // ~0.006 km/min (nearly stationary)
    private static final double DRIFT_MAX  = 0.5 * KNOT_TO_KM_PER_MIN;  // ~0.015 km/min
    private static final double RETURN_MIN = 3.0 * KNOT_TO_KM_PER_MIN;  // ~0.093 km/min
    private static final double RETURN_MAX = 4.0 * KNOT_TO_KM_PER_MIN;  // ~0.123 km/min

    @Scheduled(fixedRate = 60_000, initialDelay = 5_000)
    @Transactional
    public void simulate() {
        List<IotDevice> devices = deviceRepository.findAll();
        if (devices.isEmpty()) {
            return;
        }

        int count = 0;
        Instant now = Instant.now();

        for (IotDevice device : devices) {
            BoatState state = states.computeIfAbsent(device.getDeviceId(), this::initState);
            tickPosition(state);

            // Docked boats still emit GPS (at origin)
            double[] pos = computePosition(state);
            double lat = pos[0];
            double lng = pos[1];

            IotDeviceLocation location = new IotDeviceLocation();
            location.setDevice(device);
            location.setLatitude(lat);
            location.setLongitude(lng);
            location.setDeviceTimestamp(now);
            location.setReceivedAt(now);
            location.setEventId(device.getDeviceId() + "-" + UUID.randomUUID().toString().substring(0, 8));
            locationRepository.save(location);

            device.setLastLatitude(lat);
            device.setLastLongitude(lng);
            device.setLastDeviceTimestamp(now);
            device.setLastSeen(now);
            deviceRepository.save(device);

            count++;
        }

        log.info("[IoT-Sim] Tick — {} devices | {}",
                count,
                states.values().stream()
                        .map(s -> String.format("%s:%s@%.1fkm", s.deviceId, s.phase, s.currentDistKm))
                        .reduce((a, b) -> a + " " + b).orElse(""));
    }

    // ── State management ────────────────────────────────────────

    private BoatState initState(String deviceId) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        double bearing = BEARINGS.getOrDefault(deviceId, 180.0);
        BoatState s = new BoatState(deviceId, bearing);

        // Stagger: some boats start already out fishing
        if (rng.nextBoolean()) {
            s.currentDistKm = rng.nextDouble(0.3, 2.0);
            s.phase = Phase.FISHING;
            s.phaseTicksRemaining = rng.nextInt(20, 61);
            s.fishingStopsLeft = rng.nextInt(1, 3);
        }
        return s;
    }

    private void tickPosition(BoatState s) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();

        switch (s.phase) {
            case CRUISING -> {
                // Short cruise between fishing spots: 3-4 knots for 5-10 minutes
                double speed = rng.nextDouble(CRUISE_MIN, CRUISE_MAX);
                s.currentDistKm += speed;
                s.phaseTicksRemaining--;
                if (s.phaseTicksRemaining <= 0) {
                    // Arrived at next fishing spot — stop and fish
                    s.phase = Phase.FISHING;
                    s.phaseTicksRemaining = rng.nextInt(20, 61); // fish 20-60 minutes
                }
            }
            case FISHING -> {
                // Nearly stationary — just current drift: 0.2-0.5 knots
                double driftDist = rng.nextDouble(DRIFT_MIN, DRIFT_MAX);
                double driftSign = rng.nextBoolean() ? 1.0 : -1.0;
                s.currentDistKm += driftDist * driftSign;
                s.currentDistKm = Math.max(0.05, s.currentDistKm);
                // Slow lateral wander while drifting
                s.lateralDriftAccum += rng.nextDouble(-1.0, 1.0);

                s.phaseTicksRemaining--;
                if (s.phaseTicksRemaining <= 0) {
                    s.fishingStopsLeft--;
                    if (s.fishingStopsLeft > 0) {
                        // Move to next fishing spot
                        s.phase = Phase.CRUISING;
                        s.phaseTicksRemaining = rng.nextInt(5, 11); // cruise 5-10 min
                    } else {
                        // Done fishing, head home
                        s.phase = Phase.RETURNING;
                    }
                }
            }
            case RETURNING -> {
                double speed = rng.nextDouble(RETURN_MIN, RETURN_MAX);
                s.currentDistKm -= speed;
                s.lateralDriftAccum *= 0.9;
                if (s.currentDistKm <= 0.05) {
                    s.currentDistKm = 0.0;
                    s.phase = Phase.DOCKED;
                    s.phaseTicksRemaining = rng.nextInt(10, 30); // rest 10-30 min
                    s.lateralDriftAccum = 0;
                }
            }
            case DOCKED -> {
                s.phaseTicksRemaining--;
                if (s.phaseTicksRemaining <= 0) {
                    // New trip: cruise out, then fish at 2-4 spots
                    s.phase = Phase.CRUISING;
                    s.phaseTicksRemaining = rng.nextInt(8, 16); // initial cruise 8-15 min
                    s.fishingStopsLeft = rng.nextInt(2, 5); // 2-4 fishing stops per trip
                    s.currentDistKm = 0.05;
                }
            }
        }
    }

    private double[] computePosition(BoatState s) {
        double effectiveBearing = s.bearingDeg + s.lateralDriftAccum;
        // GPS noise ±1°
        effectiveBearing += ThreadLocalRandom.current().nextDouble(-1.0, 1.0);
        return moveLatLng(ORIGIN_LAT, ORIGIN_LNG, effectiveBearing, s.currentDistKm);
    }

    private static double[] moveLatLng(double lat, double lng, double bearingDeg, double distanceKm) {
        double R = 6371.0;
        double d = distanceKm / R;
        double brng = Math.toRadians(bearingDeg);
        double lat1 = Math.toRadians(lat);
        double lng1 = Math.toRadians(lng);

        double lat2 = Math.asin(
                Math.sin(lat1) * Math.cos(d) + Math.cos(lat1) * Math.sin(d) * Math.cos(brng)
        );
        double lng2 = lng1 + Math.atan2(
                Math.sin(brng) * Math.sin(d) * Math.cos(lat1),
                Math.cos(d) - Math.sin(lat1) * Math.sin(lat2)
        );

        return new double[]{
                Math.round(Math.toDegrees(lat2) * 1e7) / 1e7,
                Math.round(Math.toDegrees(lng2) * 1e7) / 1e7
        };
    }

    // ── Inner types ─────────────────────────────────────────────

    private enum Phase {
        CRUISING, FISHING, RETURNING, DOCKED
    }

    private static class BoatState {
        final String deviceId;
        final double bearingDeg;
        double currentDistKm;
        double lateralDriftAccum;
        Phase phase;
        int phaseTicksRemaining;
        int fishingStopsLeft;

        BoatState(String deviceId, double bearingDeg) {
            this.deviceId = deviceId;
            this.bearingDeg = bearingDeg;
            this.currentDistKm = 0.0;
            this.lateralDriftAccum = 0.0;
            this.phase = Phase.CRUISING;
            this.phaseTicksRemaining = ThreadLocalRandom.current().nextInt(8, 16);
            this.fishingStopsLeft = ThreadLocalRandom.current().nextInt(2, 5);
        }
    }
}
