package school.faang.user_service.dto.payment;

import java.math.BigDecimal;

public record PaymentResponseDto(
        PaymentStatus status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        CurrencyDto currency,
        String message
) {
}
