package school.faang.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class DataValidationException extends RuntimeException{

    public DataValidationException(String message) {
        super(message);
    }
}
