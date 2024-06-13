package school.faang.user_service.controller.contact;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.contact.ContactPreferenceDto;
import school.faang.user_service.service.contact.ContactPreferenceService;

@Tag(name = "Contacts")
@RestController("/contact")
@RequiredArgsConstructor
public class ContactPreferenceController {

    private final ContactPreferenceService contactPreferenceService;

    @GetMapping("/{userId}")
    public ContactPreferenceDto getContact(@PathVariable long userId) {
        return contactPreferenceService.getContact(userId);
    }
}
