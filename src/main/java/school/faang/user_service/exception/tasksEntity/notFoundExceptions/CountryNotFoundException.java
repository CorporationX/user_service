package school.faang.user_service.exception.tasksEntity.notFoundExceptions;

import school.faang.user_service.exception.tasksEntity.EntityNotFoundException;

public class CountryNotFoundException extends EntityNotFoundException {
    public CountryNotFoundException(String message) {
        super(message);
    }
}
