package school.faang.user_service.exception.event.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Getter
public class DataValidationException extends RuntimeException {
    private final List<String> validationErrors;

    public DataValidationException(String message, List<String> validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }
}
