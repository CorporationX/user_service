package school.faang.user_service.dto.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import school.faang.user_service.entity.payment.Currency;

import java.math.BigDecimal;

@Builder
public record PaymentRequestDto(
        @NotNull
        long paymentNumber,

        @Min(1)
        @NotNull
        BigDecimal amount,

        @NotNull
        Currency currency
) {
}
