package school.faang.user_service.exception;

public class RequestAlreadyAcceptedException extends RuntimeException {

    public RequestAlreadyAcceptedException(String message) {
        super(message);
    }
}
