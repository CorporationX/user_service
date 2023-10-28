package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.repository.contact.ContactPreferenceRepository;

@Service
@RequiredArgsConstructor
public class ContactPreferenceService {
    private final ContactPreferenceRepository contactPreferenceRepository;

    public ContactPreference createContactPreference(ContactPreference contactPreference) {
        return contactPreferenceRepository.save(contactPreference);
    }
}
