package school.faang.user_service.model.dto.premium;

import lombok.Builder;
import school.faang.user_service.model.entity.premium.Currency;
import school.faang.user_service.model.entity.premium.PaymentStatus;

import java.math.BigDecimal;

@Builder
public record PaymentResponseDto(
        PaymentStatus status,
        int verificationCode,
        long paymentNumber,
        BigDecimal amount,
        Currency currency,
        String message
) {
}