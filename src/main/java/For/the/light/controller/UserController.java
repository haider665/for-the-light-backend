package For.the.light.controller;

import For.the.light.entity.Role;
import For.the.light.entity.User;
import For.the.light.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    // @GetMapping("/user-info")
    // public ResponseEntity<Map<String, String>>
    // getUserInfo(@AuthenticationPrincipal OidcUser principal) {
    // // 'principal' holds the authenticated user's data
    // if (principal == null) {
    // return new ResponseEntity<>(Map.of("", ""), HttpStatus.OK);
    // }
    //
    // Map<String, String> userInfo = Map.of(
    // "name", principal.getFullName(),
    // "email", principal.getEmail(),
    // "picture", principal.getPicture(),
    // "firstName", principal.getGivenName(),
    // "lastName", principal.getFamilyName(),
    // "rawAttributes", principal.getAttributes().toString() // Contains all data
    // sent by Google
    // );
    //
    // return new ResponseEntity<>(userInfo, HttpStatus.OK);
    // }

    private final UserService userService;

    // @GetMapping("/details")
    // public Map<String, Object> me(@AuthenticationPrincipal OAuth2User user) {
    // return Map.of(
    // "name", user.getAttribute("name"),
    // "email", user.getAttribute("email"),
    // "picture", user.getAttribute("picture"),
    // "sub", user.getAttribute("sub"),
    // "roles",
    // user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()
    // );
    // }

    @GetMapping("/details")
    public Map<String, Object> getUserDetails(Authentication authentication) {
        if (authentication == null) {
            return Map.of();
        }

        // In JWT flow, principal is UserDetails (username=email).
        // In session flow, it might be OAuth2User.
        // authentication.getName() handles both (returns email/username).
        String email = authentication.getName();

        User dbUser = userService.getUserByEmail(email);

        Map<String, Object> userDetails = new HashMap<>();
        // JWT doesn't carry all Google attributes (name, picture) unless we add them to
        // claims.
        // For now, we rely on DB user or fallback.

        if (dbUser != null) {
            userDetails.put("id", dbUser.getId());
            userDetails.put("email", dbUser.getEmail());
            // If dbUser stores name/picture, use that. Otherwise, these might be null in
            // JWT flow.
            userDetails.put("name", dbUser.getName());
            userDetails.put("picture", dbUser.getPicture());
            userDetails.put("roles", dbUser.getRoles().stream()
                    .map(Role::name)
                    .collect(Collectors.toList()));
            userDetails.put("createdAt", dbUser.getCreatedAt());
            userDetails.put("lastLogin", dbUser.getLastLogin());
        } else {
            userDetails.put("email", email);
        }

        return userDetails;
    }
}
