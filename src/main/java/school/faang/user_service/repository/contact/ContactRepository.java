package school.faang.user_service.repository.contact;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.contact.Contact;

import java.util.Optional;

@Repository
public interface ContactRepository extends CrudRepository<Contact, Long> {

    Optional<Contact> findByContact(String title);
}
