package school.faang.user_service.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends ApiException {


    protected ForbiddenException(String message, String debugMessage) {
        super(message, debugMessage);
    }

    public static DataValidationException of(String message) {
        return new DataValidationException(message, message);
    }

    public static DataValidationException withCustomDebug(String userMessage, String debugMessage) {
        return new DataValidationException(userMessage, debugMessage);
    }

    @Override
    protected HttpStatus getDefaultStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
