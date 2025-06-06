package school.faang.user_service.dto.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;

@Data
public class PaymentResponseDto {
    @NotBlank
    private String status;
    @NotNull
    private Integer verificationCode;
    @NotNull
    private Long paymentNumber;
    @NotNull
    @Min(1)
    private BigDecimal amount;
    @NotNull
    private Currency currency;
    @NotBlank
    private String message;
}
