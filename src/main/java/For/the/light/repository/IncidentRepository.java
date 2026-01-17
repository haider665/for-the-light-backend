package For.the.light.repository;

import For.the.light.entity.Incident;
import For.the.light.entity.IncidentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByUserId(Long userId);
    List<Incident> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Incident> findByStatus(IncidentStatus status);
    List<Incident> findByUserIdAndStatus(Long userId, IncidentStatus status);
    List<Incident> findAllByOrderByCreatedAtDesc();

}