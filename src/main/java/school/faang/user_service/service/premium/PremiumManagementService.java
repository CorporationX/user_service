package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumManagementService {

    private final PremiumRepository premiumRepository;

    @Value("${premium.batch-size}")
    private int removerBatchSize;

    public void removeExpiredPremiums() {
        List<Premium> expiredPremiums = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());

        if (expiredPremiums.isEmpty()) {
            log.info("{} There aren't expired premiums to remove", LocalDateTime.now());
        } else {
            log.info("{} There are {} expired premiums to remove", LocalDateTime.now(), expiredPremiums.size());

            List<List<Premium>> expiredByBatches = ListUtils.partition(expiredPremiums, removerBatchSize);
            expiredByBatches.forEach(this::removeExpiredPremiumsByBatches);
        }
    }

    @Async("premiumThreadPools")
    public void removeExpiredPremiumsByBatches(List<Premium> expiredPremiums) {
        if (!expiredPremiums.isEmpty()) {
            premiumRepository.deleteAllInBatch(expiredPremiums);

            log.info("{} Removed {} expired premiums", LocalDateTime.now(), expiredPremiums.size());
        }
    }
}
