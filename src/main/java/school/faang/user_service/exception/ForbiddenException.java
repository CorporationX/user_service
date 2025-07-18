package school.faang.user_service.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends ApiException {
    public ForbiddenException(String message) {
        super(message, message);
    }

    public ForbiddenException(String message, String debugMessage) {
        super(message, debugMessage);
    }


    @Override
    protected HttpStatus getDefaultStatus() {
        return HttpStatus.FORBIDDEN;
    }
}
