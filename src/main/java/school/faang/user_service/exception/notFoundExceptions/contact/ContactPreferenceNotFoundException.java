package school.faang.user_service.exception.notFoundExceptions.contact;

import school.faang.user_service.exception.EntityNotFoundException;

public class ContactPreferenceNotFoundException extends EntityNotFoundException {
    public ContactPreferenceNotFoundException(String message) {
        super(message);
    }
}
