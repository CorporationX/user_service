package school.faang.user_service.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaymentFailureException extends RuntimeException {
    public PaymentFailureException(String message) {
        super(String.format(message));
        log.error(message, this);
    }
}
