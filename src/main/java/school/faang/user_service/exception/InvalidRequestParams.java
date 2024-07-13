package school.faang.user_service.exception;

public class InvalidRequestParams extends RuntimeException {

    public InvalidRequestParams(String message) {
        super(message);
    }
}
