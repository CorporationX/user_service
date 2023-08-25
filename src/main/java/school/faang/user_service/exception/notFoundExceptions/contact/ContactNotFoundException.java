package school.faang.user_service.exception.notFoundExceptions.contact;

import school.faang.user_service.exception.EntityNotFoundException;

public class ContactNotFoundException extends EntityNotFoundException {
    public ContactNotFoundException(String mes) {
        super(mes);
    }
}
