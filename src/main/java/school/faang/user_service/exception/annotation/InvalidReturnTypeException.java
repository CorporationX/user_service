package school.faang.user_service.exception.annotation;

public class InvalidReturnTypeException extends RuntimeException {
    public InvalidReturnTypeException(String message) {
        super(message);
    }
}