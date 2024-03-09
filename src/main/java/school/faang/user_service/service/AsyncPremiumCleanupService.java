package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncPremiumCleanupService {
    private final PremiumRepository premiumRepository;
    @Value("${premium_cleanup.premium_batch_size}")
    private int premiumBatchSize;

    @Async("threadPool")
    @Transactional
    public void cleanExpiredPremiumsAsync(List<Premium> premiums) {
        log.info("Cleaning async expired premiums, batch size: {}", premiums.size());
        premiumRepository.deleteAll(premiums);
    }

    public List<List<Premium>> findExpiredPremiums() {
        List<Premium> expiredPremiums = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());
        return ListUtils.partition(expiredPremiums, premiumBatchSize);
    }
}
