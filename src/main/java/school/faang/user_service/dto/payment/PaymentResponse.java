package school.faang.user_service.dto.payment;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder

public record PaymentResponse(
        PaymentStatus status,
        int verificationCode,
        UUID paymentNumber,
        BigDecimal amount,
        Currency currency,
        String message
) {
}
