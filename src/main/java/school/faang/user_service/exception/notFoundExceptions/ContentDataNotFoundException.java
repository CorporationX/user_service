package school.faang.user_service.exception.notFoundExceptions;

import school.faang.user_service.exception.EntityNotFoundException;

public class ContentDataNotFoundException extends EntityNotFoundException {
    public ContentDataNotFoundException(String message) {
        super(message);
    }
}
