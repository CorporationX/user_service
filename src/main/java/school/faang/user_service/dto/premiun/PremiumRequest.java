package school.faang.user_service.dto.premiun;

import school.faang.user_service.entity.premium.Currency;

public record PremiumRequest(
        Integer days,
        Currency currency
) {
}