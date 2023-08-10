package school.faang.user_service.exception.invalidFieldException;

import school.faang.user_service.exception.InvalidFieldException;

public class DataValidationException extends InvalidFieldException {
    public DataValidationException(String message) {
        super(message);
    }
}
