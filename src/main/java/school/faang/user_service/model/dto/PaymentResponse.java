package school.faang.user_service.model.dto;

import school.faang.user_service.model.enums.Currency;
import school.faang.user_service.model.enums.PaymentStatus;

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
