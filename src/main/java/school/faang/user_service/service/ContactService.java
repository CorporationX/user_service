package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.ContactDto;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.ContactMapper;
import school.faang.user_service.repository.contact.ContactRepository;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactMapper contactMapper;
    private final ContactRepository contactRepository;

    public ContactDto getContact(String title) {
        Contact contact = contactRepository.findByContact(title)
                .orElseThrow(() -> new EntityNotFoundException("Contact not found"));
        return contactMapper.toDto(contact);
    }
}
