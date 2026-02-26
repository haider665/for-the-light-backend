package For.the.light.repository;

import For.the.light.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByIncidentIdOrderByCreatedAtDesc(Long incidentId);
}
