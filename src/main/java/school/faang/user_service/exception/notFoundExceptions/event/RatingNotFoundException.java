package school.faang.user_service.exception.notFoundExceptions.event;

import school.faang.user_service.exception.EntityNotFoundException;

public class RatingNotFoundException extends EntityNotFoundException {
    public RatingNotFoundException(String message) {
        super(message);
    }
}
