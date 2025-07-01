package school.faang.user_service.service.contact;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.repository.contact.ContactPreferenceRepository;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {
    private final ContactPreferenceRepository contactPreferenceRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<ContactPreference> getAllContact() {
        return contactPreferenceRepository.findAll();
    }

    @Transactional
    public ContactPreference setPreferenceContactForUser(long userId, PreferredContact preference) {
        User user = userService.getUserByIdOrThrow(userId);

        ContactPreference contactPreference = new ContactPreference();
        contactPreference.setPreference(preference);
        contactPreference.setUser(user);

        ContactPreference savedContact = contactPreferenceRepository.save(contactPreference);
        log.info("Contact Preference {} has been saved", savedContact);

        return savedContact;
    }
}
