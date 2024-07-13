package school.faang.user_service.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlreadyPurchasedException extends IllegalArgumentException {
    public AlreadyPurchasedException(String message) {
        super(message);
        log.error(message, this);
    }
}
