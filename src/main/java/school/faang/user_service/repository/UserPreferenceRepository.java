package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.contact.ContactPreference;

import java.util.List;

@Repository
public interface UserPreferenceRepository extends JpaRepository<ContactPreference, Long> {
    @Query(nativeQuery = true, value = """
            select preference from contact_preferences
            where user_id = :user_id
            """)
    List<Long> getUserPreference(long user_id);
}
