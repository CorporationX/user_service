package school.faang.user_service.service;

import school.faang.user_service.model.dto.premium.PremiumDto;
import school.faang.user_service.model.entity.premium.Premium;
import school.faang.user_service.model.entity.premium.PremiumPeriod;

import java.util.List;

public interface PremiumService {

    PremiumDto buyPremium(long id, PremiumPeriod period);

    void deleteExpiredPremiums();

    void deleteExpiredPremiumsByBatches(List<Premium> expiredPremiums);
}