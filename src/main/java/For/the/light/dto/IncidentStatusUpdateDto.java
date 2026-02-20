package For.the.light.dto;

import For.the.light.entity.IncidentStatus;
import lombok.Data;

@Data
public class IncidentStatusUpdateDto {
    private IncidentStatus status;
}
