package For.the.light.iot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "iot_devices", uniqueConstraints = @UniqueConstraint(name = "uk_iot_devices_device_id", columnNames = "device_id"))
public class IotDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false, length = 100)
    private String deviceId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "api_key_hash")
    private String apiKeyHash;

    @Column(name = "hmac_secret_hash")
    private String hmacSecretHash;

    @Column(name = "last_latitude")
    private Double lastLatitude;

    @Column(name = "last_longitude")
    private Double lastLongitude;

    @Column(name = "last_device_timestamp")
    private Instant lastDeviceTimestamp;

    @Column(name = "last_seen")
    private Instant lastSeen;

    @CreationTimestamp
    @Column(name = "created", nullable = false, updatable = false)
    private Instant created;

    @UpdateTimestamp
    @Column(name = "updated", nullable = false)
    private Instant updated;
}
