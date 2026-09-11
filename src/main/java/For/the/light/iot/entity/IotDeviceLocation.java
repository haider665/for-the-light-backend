package For.the.light.iot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "iot_device_locations", uniqueConstraints = @UniqueConstraint(name = "uk_iot_device_locations_event", columnNames = {
        "device_pk", "event_id" }))
public class IotDeviceLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_pk", nullable = false)
    private IotDevice device;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "device_timestamp", nullable = false)
    private Instant deviceTimestamp;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @Column(name = "event_id", length = 150)
    private String eventId;

    @CreationTimestamp
    @Column(name = "created", nullable = false, updatable = false)
    private Instant created;

    @UpdateTimestamp
    @Column(name = "updated", nullable = false)
    private Instant updated;
}
