package school.faang.user_service.dto.client;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import school.faang.user_service.entity.premium.Currency;

import java.math.BigDecimal;

@Builder
public record PaymentRequest(
        @NotNull
        long userId,

        @Min(1)
        @NotNull
        BigDecimal amount,

        @NotNull
        Currency currency
) {
}
