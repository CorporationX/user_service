package school.faang.user_service.exception.invalidFieldException;

import school.faang.user_service.exception.InvalidFieldException;

public class InvalidIdException extends InvalidFieldException {
    public InvalidIdException(String message) {
        super(message);
    }
}
