package school.faang.user_service.model.dto.premium;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import school.faang.user_service.model.entity.premium.Currency;

import java.math.BigDecimal;

@Builder
public record PaymentRequestDto(
        @NotNull
        @Positive
        long paymentNumber,

        @Min(1)
        @NotNull
        BigDecimal amount,

        @NotNull
        Currency currency
) {
}