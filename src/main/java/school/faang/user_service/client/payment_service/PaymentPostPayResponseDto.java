package school.faang.user_service.client.payment_service;

import java.math.BigDecimal;

public record PaymentPostPayResponseDto(
        PaymentStatus status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        Currency currency,
        String message
) {
}
