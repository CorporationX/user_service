package school.faang.user_service.controller.contactPreference;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.service.contactPreference.ContactPreferenceService;

@RestController
@Slf4j
@RequestMapping("/contactPreference")
@RequiredArgsConstructor
public class ContactPreferenceController {
    private final ContactPreferenceService contactPreferenceService;
    @GetMapping("/{userId}")
    PreferredContact getPreferredContact(@PathVariable long userId) {
        PreferredContact contact = contactPreferenceService.getPreferredContact(userId);
        log.info("Returning preferred contact {} for userId={}", contact, userId);
        return contact;
    }
}
