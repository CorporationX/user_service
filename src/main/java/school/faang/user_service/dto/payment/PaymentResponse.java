package school.faang.user_service.dto.payment;

import school.faang.user_service.entity.payment.Currency;
import school.faang.user_service.entity.payment.PaymentStatus;

import java.math.BigDecimal;

public record PaymentResponse(
        PaymentStatus status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        Currency currency,
        String message
) {
}
