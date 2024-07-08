package school.faang.user_service.dto;

import java.math.BigDecimal;

public record PaymentRequest(
    long paymentNumber,
    BigDecimal amount,
    Currency currency
) {
}
