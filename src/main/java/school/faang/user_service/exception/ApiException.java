package school.faang.user_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String debugMessage;

    protected ApiException(String message, String debugMessage) {
        super(message);
        this.debugMessage = debugMessage;
        this.status = getDefaultStatus();
    }

    protected abstract HttpStatus getDefaultStatus();
}
