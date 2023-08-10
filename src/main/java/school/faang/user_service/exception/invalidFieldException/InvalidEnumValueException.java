package school.faang.user_service.exception.invalidFieldException;

import school.faang.user_service.exception.InvalidFieldException;

public class InvalidEnumValueException extends InvalidFieldException {
    public InvalidEnumValueException(String message) {
        super(message);
    }
}
