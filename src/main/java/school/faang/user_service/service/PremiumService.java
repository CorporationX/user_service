package school.faang.user_service.service;

import org.springframework.scheduling.annotation.Async;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;

import java.util.List;

public interface PremiumService {

    PremiumDto buyPremium(long id, PremiumPeriod period);

    void deleteExpiredPremiums();

    @Async("fixedThreadPools")
    void deleteExpiredPremiumsByBatches(List<Premium> expiredPremiums);
}