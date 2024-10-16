package school.faang.user_service.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.model.entity.ContactPreference;

@Repository
public interface ContactPreferenceRepository extends CrudRepository<ContactPreference, Long> {
}
