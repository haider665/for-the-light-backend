package For.the.light.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDTO {

    @NotBlank(message = "Comment content must not be blank")
    private String content;
}
