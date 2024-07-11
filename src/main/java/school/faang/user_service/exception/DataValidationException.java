package school.faang.user_service.exception;

import org.springframework.stereotype.Component;

@Component
public class DataValidationException extends RuntimeException{
    public DataValidationException(String message) {
        super(message);
    }
}
