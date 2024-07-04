package school.faang.user_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ValidationException extends RuntimeException{
    private String message;
    public ValidationException(String message) {
        super(message);
        this.message = message;
    }
}
