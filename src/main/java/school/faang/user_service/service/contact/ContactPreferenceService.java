package school.faang.user_service.service.contact;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.contact.ContactPreferenceDto;
import school.faang.user_service.mapper.contact.ContactPreferenceMapper;
import school.faang.user_service.repository.contact.ContactPreferenceRepository;

@Service
@RequiredArgsConstructor
public class ContactPreferenceService {

    private final ContactPreferenceRepository contactPreferenceRepository;
    private final ContactPreferenceMapper contactPreferenceMapper;

    @Transactional
    public void save(ContactPreferenceDto contactPreferenceDto) {
        contactPreferenceRepository.save(contactPreferenceMapper.toEntity(contactPreferenceDto));
    }
}
