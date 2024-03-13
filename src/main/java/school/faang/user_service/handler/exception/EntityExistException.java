package school.faang.user_service.handler.exception;

import org.springframework.http.HttpStatus;

public class EntityExistException extends RuntimeException {
    private HttpStatus httpStatus;

    public EntityExistException(String message) {
        super(message);
    }
}