package school.faang.service.user.repository.contact;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.service.user.entity.contact.ContactPreference;

@Repository
public interface ContactPreferenceRepository extends CrudRepository<ContactPreference, Long> {
}
