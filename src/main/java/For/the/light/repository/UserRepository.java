package For.the.light.repository;

import For.the.light.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    java.util.Optional<User> findByProviderAndProviderId(String provider, String providerId);

    java.util.Optional<User> findByEmail(String email);
}
