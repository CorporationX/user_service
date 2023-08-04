package school.faang.user_service.exception.tasksEntity.notFoundExceptions.contact;

import school.faang.user_service.exception.tasksEntity.notFoundExceptions.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(String mes) {
        super(mes);
    }
}
