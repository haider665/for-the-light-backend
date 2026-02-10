package For.the.light.dto;

import For.the.light.entity.ProgramStatus;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import java.time.ZonedDateTime;

@Data
public class ProgramDto {
    private Long id;
    private String title;
    private String description;
    private String displayImage;
    private String programSchedule;
    private ZonedDateTime programStartDate;
    private ZonedDateTime registrationDeadline;
    private ProgramStatus status;
    private String createdBy;
    private Long version;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private boolean isEnrolled;
}
