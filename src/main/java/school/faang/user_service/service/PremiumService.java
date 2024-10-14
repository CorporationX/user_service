package school.faang.user_service.service;

import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.PremiumPeriod;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PremiumService {

    PremiumDto buyPremium(long userId, PremiumPeriod premiumPeriod);

    List<List<Long>> findAndSplitExpiredPremiums();

    CompletableFuture<Integer> deleteExpiredPremiumsByIds(List<Long> ids);
}
