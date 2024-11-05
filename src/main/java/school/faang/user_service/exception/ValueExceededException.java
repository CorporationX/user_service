package school.faang.user_service.exception;

public class ValueExceededException extends RuntimeException {

    public ValueExceededException(String message) {
        super(message);
    }
}
