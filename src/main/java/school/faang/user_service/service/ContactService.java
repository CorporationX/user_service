package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.repository.contact.ContactRepository;

@Service
@RequiredArgsConstructor
public class ContactService {
    private final ContactRepository contactRepository;

    public void save(Contact contact) {
        contactRepository.save(contact);
    }
}
