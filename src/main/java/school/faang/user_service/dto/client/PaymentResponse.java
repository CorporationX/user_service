package school.faang.user_service.dto.client;

import java.math.BigDecimal;

public record PaymentResponse(
        String status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        Currency paymentCurrency,
        Currency targetCurrency,
        String message
) {
}
