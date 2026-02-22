package For.the.light.dto;

import For.the.light.entity.IncidentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class IncidentResponseDTO {

    private Long id;
    private String title;
    private String description;
    private IncidentStatus status;
    private LocationDTO location;
    private List<String> images;
    private Long userId;
    private String userName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String videoUrl;
    private List<CommentResponseDTO> comments = new ArrayList<>();
}