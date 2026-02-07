package For.the.light.service;

import For.the.light.entity.Role;
import For.the.light.entity.User;
import For.the.light.repository.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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

    @Transactional
    public void updateUserRoles(Long targetUserId, Set<Role> newRoles, String currentUserEmail) {
        User currentUser = getUserByEmail(currentUserEmail);

        if (currentUser == null) {
            throw new IllegalArgumentException("Current user not found");
        }

        // Prevent users from modifying their own roles
        if (currentUser.getId().equals(targetUserId)) {
            throw new IllegalArgumentException("You cannot modify your own roles");
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Target user not found"));

        // Add new roles to existing ones (don't clear)
        targetUser.getRoles().addAll(newRoles);
        userRepository.save(targetUser);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}