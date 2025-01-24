package school.faang.user_service.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PaymentRequest {
    @NotNull
    private long paymentNumber;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private Currency currency;
}
