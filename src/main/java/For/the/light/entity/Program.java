package For.the.light.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "programs")
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String displayImage;

    private String programSchedule;

    private ZonedDateTime programStartDate;

    private ZonedDateTime registrationDeadline;

    @Enumerated(EnumType.STRING)
    private ProgramStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "program_enrollments", joinColumns = @JoinColumn(name = "program_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> enrolledUsers = new HashSet<>();

    @Version
    private Long version;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;
}
