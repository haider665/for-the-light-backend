package For.the.light.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class HomeController {

    @GetMapping("/live")
    public ResponseEntity<String> home() {
        return new ResponseEntity<>("Hello World!", HttpStatus.OK);
    }

    @GetMapping("/test")
    public ResponseEntity test() {
        log.info("Hello World test!");
        return new ResponseEntity("Hello World Test!", HttpStatus.OK);
    }

    @GetMapping("/user-info")
    public ResponseEntity<Map<String, String>> getUserInfo(@AuthenticationPrincipal OidcUser principal) {
        // 'principal' holds the authenticated user's data
        if (principal == null) {
            return new ResponseEntity<>(Map.of("", ""), HttpStatus.OK);
        }

        Map<String, String> userInfo = Map.of(
                "name", principal.getFullName(),
                "email", principal.getEmail(),
                "picture", principal.getPicture(),
                "firstName", principal.getGivenName(),
                "lastName", principal.getFamilyName(),
                "rawAttributes", principal.getAttributes().toString() // Contains all data sent by Google
        );

        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }
}
