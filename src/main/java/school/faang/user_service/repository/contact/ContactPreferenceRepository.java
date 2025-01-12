package school.faang.user_service.repository.contact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.contact.ContactPreference;

@Repository
public interface ContactPreferenceRepository extends JpaRepository<ContactPreference, Long> {

}
