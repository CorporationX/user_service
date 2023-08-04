package school.faang.user_service.exception.invalidFieldException;

import school.faang.user_service.exception.InvalidFieldException;

public class EntityIsNullOrEmptyException extends InvalidFieldException {
    public EntityIsNullOrEmptyException(String message) {
        super(message);
    }
}
