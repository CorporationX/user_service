package school.faang.user_service.exception.tasksEntity.invalidFieldException;

import school.faang.user_service.exception.tasksEntity.InvalidFieldException;

public class InvalidIdException extends InvalidFieldException {
    public InvalidIdException(String message) {
        super(message);
    }
}
