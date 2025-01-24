package school.faang.user_service.service.premium;

import school.faang.user_service.dto.entity.premium.PremiumPeriod;
import school.faang.user_service.dto.premium.PremiumDto;

public interface PremiumService {
    PremiumDto buyPremium(long userid, long paymentNumber, PremiumPeriod premiumPeriod);
}
