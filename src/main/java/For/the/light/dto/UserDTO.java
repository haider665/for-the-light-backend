package For.the.light.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private String picture;
    private List<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}
