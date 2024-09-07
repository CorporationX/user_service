package school.faang.user_service.dto.client;

import lombok.Builder;
import school.faang.user_service.entity.premium.Currency;

import java.math.BigDecimal;

@Builder
public record PaymentResponse(
        PaymentStatus status,
        int verificationCode,
        long userId,
        BigDecimal amount,
        Currency currency,
        String message
) {
}