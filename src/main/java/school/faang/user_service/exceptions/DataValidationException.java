package school.faang.user_service.exceptions;

import java.util.zip.DataFormatException;

public class DataValidationException extends RuntimeException {
    public DataValidationException(String message) {
        super(message);
    }

    public DataValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
