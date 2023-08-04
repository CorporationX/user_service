package school.faang.user_service.exception;

public class SameEntityException extends RuntimeException{
    public SameEntityException(String message) {
        super(message);
    }
}
