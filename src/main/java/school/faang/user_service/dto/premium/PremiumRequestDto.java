package school.faang.user_service.dto.premium;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.payment.CurrencyDto;
import school.faang.user_service.enums.premium.PremiumType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PremiumRequestDto {
    @NotNull(message = "Premium type can't be null")
    private PremiumType premiumType;

    @NotNull(message = "user id can't be null")
    private Long userId;

    @NotNull(message = "selected currency can't be null")
    private CurrencyDto selectedCurrency;
    private boolean autoRenew;
}
