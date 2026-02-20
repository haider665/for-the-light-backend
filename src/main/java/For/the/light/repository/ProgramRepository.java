package For.the.light.repository;

import For.the.light.entity.Program;
import For.the.light.entity.ProgramStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {
    List<Program> findAllByStatusAndRegistrationDeadlineAfter(ProgramStatus status, ZonedDateTime now);
    List<Program> findAllByStatus(ProgramStatus status);
}
