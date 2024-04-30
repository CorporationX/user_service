package school.faang.user_service.exception;

public class EmptyGoalTitleException extends RuntimeException {
    public EmptyGoalTitleException() {
        super("Goal title cannot be empty");
    }
}
