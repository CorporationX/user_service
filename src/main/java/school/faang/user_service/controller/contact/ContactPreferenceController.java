package school.faang.user_service.controller.contact;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.contact.ContactPreferenceDto;
import school.faang.user_service.service.contact.ContactPreferenceService;

@Tag(name = "Contacts")
@RestController
@RequestMapping("/contacts/preferences")
@RequiredArgsConstructor
public class ContactPreferenceController {

    private final ContactPreferenceService contactPreferenceService;

    @GetMapping("/{userName}")
    public ContactPreferenceDto getContactPreference(@PathVariable String userName) {
        return contactPreferenceService.getContact(userName);
    }
}
