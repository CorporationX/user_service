package school.faang.user_service.exception.tasksEntity.invalidFieldException;

import school.faang.user_service.exception.tasksEntity.InvalidFieldException;

public class EntityIsNullOrEmptyException extends InvalidFieldException {
    public EntityIsNullOrEmptyException(String message) {
        super(message);
    }
}
