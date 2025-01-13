package school.faang.user_service.repository.contact;

import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.dto.entity.contact.ContactPreference;

public interface ContactPreferenceRepository extends CrudRepository<ContactPreference, Long> {
}
