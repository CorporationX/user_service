package school.faang.user_service.client.paymentService.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentResponse(
        @NotNull
        PaymentStatus status,
        @NotBlank
        String requestId,
        @NotNull
        Product product,
        @NotNull
        Long paymentNumber,
        @NotNull
        Long paidDateTime
) {
}
