package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.payment.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PremiumBoughtEvent {
    @NotNull
    Long userId;
    @NotNull
    BigDecimal amount;
    @NotNull
    Currency currency;
    @NotNull
    int days;
    @NotNull
    LocalDateTime timestamp;
}
