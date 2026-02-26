package For.the.light.dto;

import For.the.light.entity.Role;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserRoleUpdateDto {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotEmpty(message = "At least one role must be provided")
    private Set<Role> roles;
}
