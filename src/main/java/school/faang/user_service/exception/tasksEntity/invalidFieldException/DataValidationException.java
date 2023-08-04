package school.faang.user_service.exception.tasksEntity.invalidFieldException;

import school.faang.user_service.exception.tasksEntity.InvalidFieldException;

public class DataValidationException extends InvalidFieldException {
    public DataValidationException(String message) {
        super(message);
    }
}
