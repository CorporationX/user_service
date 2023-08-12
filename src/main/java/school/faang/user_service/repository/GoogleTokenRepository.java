package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.GoogleToken;
import school.faang.user_service.entity.User;

import java.util.List;

@Repository
public interface GoogleTokenRepository extends JpaRepository<GoogleToken, Long> {
    @Query(nativeQuery = true, value = """
            SELECT gt.oauth_client_id FROM google_token gt
            """)
    List<String> findAllOauthClientId();

    GoogleToken findByOauthClientId(String clientId);

    GoogleToken findByUser(User user);
}
