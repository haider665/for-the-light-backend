package For.the.light.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "incidents")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column
    private String title;

    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentStatus status = IncidentStatus.PENDING;

    @Embedded
    private Location location;

    @ElementCollection
    @CollectionTable(name = "incident_images", joinColumns = @JoinColumn(name = "incident_id"))
    @Column(name = "image_url", length = 1000)
    private List<String> images = new ArrayList<>();

    @Column(name = "video_url", length = 1000)
    private String videoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Incident() {
    }

    public Incident(String title, String description, User user) {
        this.title = title;
        this.description = description;
        this.user = user;
        this.status = IncidentStatus.PENDING;
    }
}