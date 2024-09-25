package school.faang.user_service.dto.premiun;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import school.faang.user_service.entity.premium.Currency;

@Builder
public record PremiumRequest(

        @NotNull
        Integer days,

        @NotNull
        Currency currency
) {
}