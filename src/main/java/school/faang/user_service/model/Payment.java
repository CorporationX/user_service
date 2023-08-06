package school.faang.user_service.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.validation.annotation.ValidCurrency;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @NotNull
    private Long paymentNumber;

    @NotNull
    private BigDecimal amount;

    @NotBlank
    @ValidCurrency
    private String currency;
}
