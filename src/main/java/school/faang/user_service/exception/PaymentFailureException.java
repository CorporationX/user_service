package school.faang.user_service.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaymentFailureException extends RuntimeException {
    public PaymentFailureException(long paymentNumber) {
        super(String.format("Payment %d failed", paymentNumber));
        log.error("Payment {} failed", paymentNumber, this);
    }
}
