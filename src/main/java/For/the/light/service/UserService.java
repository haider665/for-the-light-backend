package For.the.light.service;


import For.the.light.entity.Role;
import For.the.light.entity.User;
import For.the.light.repository.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User saveOrUpdateUser(OAuth2User oAuth2User, String provider) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");
        String providerId = oAuth2User.getAttribute("sub");

        return userRepository.findByProviderAndProviderId(provider, providerId)
                .map(existingUser -> {
                    existingUser.setName(name);
                    existingUser.setPicture(picture);
                    existingUser.setLastLogin(LocalDateTime.now());
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    User newUser = new User(email, name, picture, provider, providerId);
                    newUser.setLastLogin(LocalDateTime.now());
                    // Default role USER is already added in constructor
                    return userRepository.save(newUser);
                });
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Transactional
    public void addRoleToUser(Long userId, Role role) {
        userRepository.findById(userId).ifPresent(user -> {
            user.addRole(role);
            userRepository.save(user);
        });
    }

    @Transactional
    public void removeRoleFromUser(Long userId, Role role) {
        userRepository.findById(userId).ifPresent(user -> {
            user.removeRole(role);
            userRepository.save(user);
        });
    }
}