package school.faang.user_service.service.contact;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.contact.ContactDto;
import school.faang.user_service.mapper.contact.ContactMapper;
import school.faang.user_service.repository.contact.ContactRepository;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;

    @Transactional
    public void save(ContactDto contactDto) {
        contactRepository.save(contactMapper.toEntity(contactDto));
    }
}
