package school.faang.user_service.repository.contact;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.contact.ContactPreference;

import java.util.Optional;

@Repository
public interface ContactPreferenceRepository extends CrudRepository<ContactPreference, Long> {

    @Query(nativeQuery = true, value = """
            SELECT * FROM contact_preferences cp
            WHERE cp.user_id = ?1
            """)
    Optional<ContactPreference> findByUserId(long userId);
}
