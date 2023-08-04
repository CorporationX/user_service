package school.faang.user_service.exception;

public class IncorrectStatusException extends RuntimeException{
    public IncorrectStatusException(String message) {
        super(message);
    }
}
