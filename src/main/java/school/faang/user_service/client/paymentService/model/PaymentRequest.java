package school.faang.user_service.client.paymentService.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotBlank
        String requestId,

        @Min(1)
        @NotNull
        BigDecimal amount,

        @NotNull
        Currency currency,

        @NotNull
        Product product
) {
}
