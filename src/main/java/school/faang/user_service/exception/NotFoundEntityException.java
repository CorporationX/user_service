package school.faang.user_service.exception;

public class NotFoundEntityException extends RuntimeException{

    public NotFoundEntityException(String message) {
        super(message);
    }
}
