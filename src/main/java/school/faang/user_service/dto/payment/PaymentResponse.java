package school.faang.user_service.dto.payment;

import lombok.Getter;

import java.math.BigDecimal;

public record PaymentResponse(
        @Getter
        PaymentStatus status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        Currency currency,
        String message
) {
}
