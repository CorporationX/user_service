package school.faang.user_service.dto;

public record PaymentResponse(
    PaymentStatus status,
    int verificationCode,
    long paymentNumber,
    double amount,
    Currency currency,
    String message
) {
}
