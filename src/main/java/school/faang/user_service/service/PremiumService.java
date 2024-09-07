package school.faang.user_service.service;

import school.faang.user_service.dto.premiun.PremiumDto;
import school.faang.user_service.entity.premium.Currency;

public interface PremiumService {

    PremiumDto buyPremium(long id, Integer days, Currency currency);
}
