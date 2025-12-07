package school.faang.user_service.client.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import school.faang.user_service.enums.Currency;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull
        long paymentNumber,

        @Min(1)
        @NotNull
        BigDecimal amount,

        @NotNull
        Currency currency
) {
}
