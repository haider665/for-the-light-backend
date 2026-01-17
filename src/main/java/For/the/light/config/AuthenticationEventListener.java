package For.the.light.config;

import For.the.light.entity.User;
import For.the.light.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEventListener {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationEventListener.class);
    private final UserService userService;

    public AuthenticationEventListener(UserService userService) {
        this.userService = userService;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        if (event.getAuthentication() instanceof OAuth2LoginAuthenticationToken) {
            logger.info("OAuth2 authentication event received");

            OAuth2LoginAuthenticationToken token = (OAuth2LoginAuthenticationToken) event.getAuthentication();
            OAuth2User oAuth2User = token.getPrincipal();

            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            String sub = oAuth2User.getAttribute("sub");

            logger.info("Saving user - Email: {}, Name: {}, Sub: {}", email, name, sub);

            try {
                User user = userService.saveOrUpdateUser(oAuth2User, "google");
                logger.info("User saved successfully with ID: {}", user.getId());
            } catch (Exception e) {
                logger.error("Error saving user", e);
            }
        }
    }
}