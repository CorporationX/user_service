package school.faang.user_service.dto.payment;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentResponse {
    private PaymentStatus status;
    private int verificationCode;
    private long paymentNumber;
    private BigDecimal amount;
    private Currency currency;
    private String message;
}
