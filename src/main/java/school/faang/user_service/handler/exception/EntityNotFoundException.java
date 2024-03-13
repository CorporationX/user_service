package school.faang.user_service.handler.exception;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends RuntimeException {
    private HttpStatus httpStatus;

    public EntityNotFoundException(String message) {
        super(message);
    }

}