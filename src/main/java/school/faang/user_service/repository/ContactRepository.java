package school.faang.user_service.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.model.entity.Contact;

@Repository
public interface ContactRepository extends CrudRepository<Contact, Long> {
}
