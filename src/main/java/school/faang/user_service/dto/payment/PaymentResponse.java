package school.faang.user_service.dto.payment;

import school.faang.user_service.dto.payment.types.Currency;
import school.faang.user_service.dto.payment.types.PaymentStatus;

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
