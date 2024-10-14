package school.faang.user_service.service.impl;

import school.faang.user_service.model.dto.PremiumDto;
import school.faang.user_service.model.enums.PremiumPeriod;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PremiumService {

    PremiumDto buyPremium(long userId, PremiumPeriod premiumPeriod);

    List<List<Long>> findAndSplitExpiredPremiums();

    CompletableFuture<Integer> deleteExpiredPremiumsByIds(List<Long> ids);
}
