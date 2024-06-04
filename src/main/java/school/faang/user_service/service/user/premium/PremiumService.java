package school.faang.user_service.service.user.premium;

import school.faang.user_service.dto.types.PremiumPeriod;

public interface PremiumService {

    void buyPremium(Long userId, PremiumPeriod premiumPeriod);
}
