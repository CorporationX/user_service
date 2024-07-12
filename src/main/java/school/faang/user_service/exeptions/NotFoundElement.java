package school.faang.user_service.exeptions;

public class NotFoundElement extends RuntimeException{

    public NotFoundElement(String message) {
        super(message);
    }

    public NotFoundElement(String message, Throwable cause) {
        super(message, cause);
    }
}
