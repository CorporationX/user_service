package school.faang.user_service.exception.tasksEntity.notFoundExceptions;

public class MentorshipRequestNotFoundException extends EntityNotFoundException {
    public MentorshipRequestNotFoundException(String message) {
        super(message);
    }
}
