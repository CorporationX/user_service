package school.faang.user_service.service.contact;

import school.faang.user_service.dto.contact.ContactPreferenceDto;

public interface ContactPreferenceService {

    ContactPreferenceDto getContact(long userId);
}
