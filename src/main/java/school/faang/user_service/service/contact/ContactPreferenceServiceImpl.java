package school.faang.user_service.service.contact;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.contact.ContactPreferenceDto;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.mapper.contact.ContactPreferenceMapper;
import school.faang.user_service.repository.contact.ContactPreferenceRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactPreferenceServiceImpl implements ContactPreferenceService {

    private final ContactPreferenceRepository contactRepository;
    private final ContactPreferenceMapper contactPreferenceMapper;

    @Override
    @Transactional(readOnly = true)
    public ContactPreferenceDto getContact(String username) {
        ContactPreference contactPreference = contactRepository.findByUserUsername(username)
                .orElseThrow(() -> new NotFoundException("Contact not found"));

        return contactPreferenceMapper.toDto(contactPreference);
    }
}
