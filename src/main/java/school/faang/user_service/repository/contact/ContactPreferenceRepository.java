package school.faang.user_service.repository.contact;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.contact.ContactPreference;

public interface ContactPreferenceRepository extends JpaRepository<ContactPreference, Long> {
}
