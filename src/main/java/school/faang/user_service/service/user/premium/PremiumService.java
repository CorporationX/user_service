package school.faang.user_service.service.user.premium;

import school.faang.user_service.dto.types.PremiumPeriod;

import java.util.List;

public interface PremiumService {

    void buyPremium(Long userId, PremiumPeriod premiumPeriod);

    void deleteAllExpiredPremium();
}
