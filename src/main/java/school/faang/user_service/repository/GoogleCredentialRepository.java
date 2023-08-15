package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.GoogleCredential;

@Repository
public interface GoogleCredentialRepository extends JpaRepository<GoogleCredential, Long> {
    @Query(nativeQuery = true, value = """
        SELECT gc.* FROM google_credentials gc
        WHERE gc.client_email = :clientEmail
    """)
    GoogleCredential findByClientEmail(String clientEmail);
}
