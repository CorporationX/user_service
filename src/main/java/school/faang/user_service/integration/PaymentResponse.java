package school.faang.user_service.integration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private String status;
    private int verificationCode;
    private Long paymentNumber;
    private BigDecimal amount;
    private String currency;
    private String message;
}
