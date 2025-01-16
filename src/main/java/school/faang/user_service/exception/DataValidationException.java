package school.faang.user_service.exception;

import lombok.Getter;

@Getter
public class DataValidationException extends RuntimeException {

    public DataValidationException(String message) {
        super(message);
    }
}
