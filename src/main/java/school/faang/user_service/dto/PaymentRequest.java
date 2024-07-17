package school.faang.user_service.dto;

import lombok.Builder;

@Builder
public record PaymentRequest(
    long paymentNumber,
    double amount,
    Currency currency
) {
}
