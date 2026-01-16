package For.the.light.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {



//    @GetMapping("/user-info")
//    public ResponseEntity<Map<String, String>> getUserInfo(@AuthenticationPrincipal OidcUser principal) {
//        // 'principal' holds the authenticated user's data
//        if (principal == null) {
//            return new ResponseEntity<>(Map.of("", ""), HttpStatus.OK);
//        }
//
//        Map<String, String> userInfo = Map.of(
//                "name", principal.getFullName(),
//                "email", principal.getEmail(),
//                "picture", principal.getPicture(),
//                "firstName", principal.getGivenName(),
//                "lastName", principal.getFamilyName(),
//                "rawAttributes", principal.getAttributes().toString() // Contains all data sent by Google
//        );
//
//        return new ResponseEntity<>(userInfo, HttpStatus.OK);
//    }

    @GetMapping("/details")
    public Map<String,Object> me(@AuthenticationPrincipal OAuth2User user) {
        return Map.of(
                "name", user.getAttribute("name"),
                "email", user.getAttribute("email"),
                "picture", user.getAttribute("picture"),
                "sub", user.getAttribute("sub"),
                "roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()
        );
    }
}
