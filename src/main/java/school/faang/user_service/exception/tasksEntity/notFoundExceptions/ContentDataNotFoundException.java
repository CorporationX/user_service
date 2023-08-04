package school.faang.user_service.exception.tasksEntity.notFoundExceptions;

public class ContentDataNotFoundException extends EntityNotFoundException{
    public ContentDataNotFoundException(String message) {
        super(message);
    }
}
