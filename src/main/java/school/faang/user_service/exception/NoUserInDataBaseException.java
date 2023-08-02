package school.faang.user_service.exception;

public class NoUserInDataBaseException extends RuntimeException {

    public NoUserInDataBaseException(String message) {
        super(message);
    }
}
