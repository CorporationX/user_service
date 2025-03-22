package school.faang.user_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class UserServiceException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String details;

    @Override
    public String toString() {
        return errorCode.getDescription() + " : " + details;
    }
}
