package school.faang.user_service.exception;

import lombok.Getter;

@Getter
public class CheckException extends RuntimeException {
    public CheckException(String message) {
        super(message);
    }

}
