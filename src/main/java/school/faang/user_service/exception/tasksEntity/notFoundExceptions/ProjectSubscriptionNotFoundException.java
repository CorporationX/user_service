package school.faang.user_service.exception.tasksEntity.notFoundExceptions;

public class ProjectSubscriptionNotFoundException extends EntityNotFoundException{
    public ProjectSubscriptionNotFoundException(String message) {
        super(message);
    }
}
