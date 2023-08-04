package school.faang.user_service.exception.tasksEntity;

public class InvalidIdException extends InvalidRequestException{
    public InvalidIdException(String message) {
        super(message);
    }
}
