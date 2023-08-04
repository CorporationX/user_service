package school.faang.user_service.exception.notFoundExceptions;

import school.faang.user_service.exception.EntityNotFoundException;

public class CountryNotFoundException extends EntityNotFoundException {
    public CountryNotFoundException(String message) {
        super(message);
    }
}
