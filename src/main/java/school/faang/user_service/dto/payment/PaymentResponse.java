package school.faang.user_service.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private PaymentStatus status;
    private int verificationCode;
    private long paymentNumber;
    private BigDecimal amount;
    private Currency currency;
    private String message;
}
