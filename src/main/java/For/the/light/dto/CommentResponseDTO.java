package For.the.light.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponseDTO {

    private Long id;
    private String content;
    private Long userId;
    private String userName;
    private Long incidentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
