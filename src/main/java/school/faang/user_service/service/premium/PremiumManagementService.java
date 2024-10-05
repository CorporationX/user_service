package school.faang.user_service.service.premium;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PremiumManagementService {

    private final PremiumRepository premiumRepository;

    @Value("${premium.batch-size}")
    private int removerBatchSize;

    @Transactional
    public void removeExpiredPremiums() {
        List<Premium> expiredPremiums = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());
        if (!expiredPremiums.isEmpty()) {
            List<List<Premium>> expiredByBatches = ListUtils.partition(expiredPremiums, removerBatchSize);

            expiredByBatches.forEach(this::removeExpiredPremiumsByBatches);
        }
    }

    @Async("premiumThreadPools")
    public void removeExpiredPremiumsByBatches(List<Premium> expiredPremiums) {
        if (!expiredPremiums.isEmpty()) {
            premiumRepository.deleteAllInBatch(expiredPremiums);
        }
    }
}
