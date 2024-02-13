package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.entity.premium.Premium;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PremiumCleanupService {
    private final PremiumRepository premiumRepository;
    private final AsyncPremiumCleanupService asyncPremiumCleanupService;
    @Value("${premium_cleanup.premium_batch_size}")
    private int premiumBatchSize;

    @Scheduled(cron = "${premium_cleanup.time}", zone = "${premium_cleanup.zone}")

    public void cleanExpiredPremiums() {
        log.info("Cleaning expired premiums");
        List<Premium> expiredPremiums = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());
        if (expiredPremiums.size() > premiumBatchSize) {
            List<List<Premium>> expiredPremiumBatches = ListUtils.partition(expiredPremiums, premiumBatchSize);
            expiredPremiumBatches.forEach(asyncPremiumCleanupService::cleanExpiredPremiumsAsync);
        } else {
            premiumRepository.deleteAll(expiredPremiums);
        }
    }
}
