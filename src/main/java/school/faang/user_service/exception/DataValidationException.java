package school.faang.user_service.exception;

import lombok.Getter;

@Getter
public class DataValidationException extends RuntimeException {

    public DataValidationException(ErrorMessages errorMessage) {
        super(errorMessage.getMessage());
    }

    public DataValidationException(String message) {
        super(message);
    }

    public DataValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}