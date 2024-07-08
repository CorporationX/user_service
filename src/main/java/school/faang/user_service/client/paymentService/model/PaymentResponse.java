package school.faang.user_service.client.paymentService.model;

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
