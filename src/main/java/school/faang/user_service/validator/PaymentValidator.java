package school.faang.user_service.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.PaymentResponse;
import school.faang.user_service.dto.PaymentStatus;
import school.faang.user_service.exception.PaymentFailureException;

@Component
public class PaymentValidator {
    public void validatePaymentSuccess(PaymentResponse paymentResponse) {
        if (paymentResponse.status() != PaymentStatus.SUCCESS) {
            throw new PaymentFailureException(String.format("Payment with payment number: %d failed.", paymentResponse.paymentNumber()));
        }
    }
}
