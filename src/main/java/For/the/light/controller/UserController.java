package For.the.light.controller;

import For.the.light.dto.UserDTO;
import For.the.light.dto.UserRoleUpdateDto;
import For.the.light.entity.Role;
import For.the.light.entity.User;
import For.the.light.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/details")
    public ResponseEntity<UserDTO> getUserDetails(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        String email = authentication.getName();
        User dbUser = userService.getUserByEmail(email);

        if (dbUser == null) {
            return ResponseEntity.notFound().build();
        }

        UserDTO response = new UserDTO(
                dbUser.getId(),
                dbUser.getEmail(),
                dbUser.getName(),
                dbUser.getPicture(),
                dbUser.getRoles().stream()
                        .map(Role::name)
                        .collect(Collectors.toList()),
                dbUser.getCreatedAt(),
                dbUser.getLastLogin());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<?> updateUserRoles(
            @PathVariable Long userId,
            @Valid @RequestBody UserRoleUpdateDto request,
            Authentication authentication) {

        try {
            String currentUserEmail = authentication.getName();
            userService.updateUserRoles(userId, request.getRoles(), currentUserEmail);

            return ResponseEntity.ok(Map.of(
                    "message", "User roles updated successfully",
                    "userId", userId,
                    "newRoles", request.getRoles().stream()
                            .map(Role::name)
                            .collect(Collectors.toList())));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()));
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();

        List<UserDTO> userList = users.stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getEmail(),
                        user.getName(),
                        user.getPicture(),
                        user.getRoles().stream()
                                .map(Role::name)
                                .collect(Collectors.toList()),
                        user.getCreatedAt(),
                        user.getLastLogin()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(userList);
    }
}
