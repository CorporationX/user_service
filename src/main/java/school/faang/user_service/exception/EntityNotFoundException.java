package school.faang.user_service.exception;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends ApiException {

    protected EntityNotFoundException(String message, String debugMessage) {
        super(message, debugMessage);
    }

    public static EntityNotFoundException of(String message) {
        return new EntityNotFoundException(message, message);
    }

    public static EntityNotFoundException withCustomDebug(String userMessage, String debugMessage) {
        return new EntityNotFoundException(userMessage, debugMessage);
    }

    @Override
    protected HttpStatus getDefaultStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
