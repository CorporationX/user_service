package school.faang.user_service.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IllegalEntityException extends IllegalArgumentException {
    public IllegalEntityException(String message) {
        super(message);
        log.error(message, this);
    }
}
