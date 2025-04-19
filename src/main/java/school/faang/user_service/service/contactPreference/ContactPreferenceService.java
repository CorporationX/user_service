package school.faang.user_service.service.contactPreference;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.messages.ErrorMessages;
import school.faang.user_service.repository.contact.ContactPreferenceRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactPreferenceService {
    private final ContactPreferenceRepository contactPreferenceRepository;

    public PreferredContact getPreferredContact(Long userId) {
        ContactPreference preference = contactPreferenceRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.getErrorNotFoundContact(userId)));
        return preference.getPreference();
    }
}
