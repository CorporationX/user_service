package school.faang.user_service.exception.notFoundExceptions.contact;

import school.faang.user_service.exception.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(String mes) {
        super(mes);
    }
}
