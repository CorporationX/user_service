package school.faang.user_service.service.premium;

import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.PremiumPeriod;

public interface PremiumService {

    PremiumDto buyPremium(long id, PremiumPeriod period);

    void deleteExpiredPremiums();
}