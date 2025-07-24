package school.faang.user_service.repository.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.user.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM refresh_tokens
            WHERE token = :token
              AND is_revoked = false
              AND expired_at > now()
            """)
    Optional<RefreshToken> getValidToken(String token);

    void deleteByToken(String token);

}