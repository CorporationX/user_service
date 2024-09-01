package school.faang.user_service.exception;

public class WriteEntityAsStringException extends RuntimeException {
    public WriteEntityAsStringException(String message, Throwable cause) {
        super(message, cause);
    }

    public WriteEntityAsStringException(Object value, Throwable cause) {
        super("Cannot serialize given profileViewEvent value as a String \n" +
                "ProfileViewEvent value " + value +"\n" +
                cause.getMessage(), cause);
    }
}
