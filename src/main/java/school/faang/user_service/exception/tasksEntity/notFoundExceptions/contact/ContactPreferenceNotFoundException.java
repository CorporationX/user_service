package school.faang.user_service.exception.tasksEntity.notFoundExceptions.contact;

import school.faang.user_service.exception.tasksEntity.EntityNotFoundException;

public class ContactPreferenceNotFoundException extends EntityNotFoundException {
    public ContactPreferenceNotFoundException(String message) {
        super(message);
    }
}
