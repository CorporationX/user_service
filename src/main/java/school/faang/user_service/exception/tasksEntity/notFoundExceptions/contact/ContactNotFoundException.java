package school.faang.user_service.exception.tasksEntity.notFoundExceptions.contact;

import school.faang.user_service.exception.tasksEntity.notFoundExceptions.EntityNotFoundException;

public class ContactNotFoundException extends EntityNotFoundException {
    public ContactNotFoundException(String mes) {
        super(mes);
    }
}
