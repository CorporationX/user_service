package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.entity.premium.Premium;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class PremiumCleanupService {
    private final PremiumRepository premiumRepository;
    @Value("${premium_cleanup.premium_batch_size}")
    private int premiumBatchSize;

    @Scheduled(cron = "${premium_cleanup.time}", zone = "${premium_cleanup.zone}")

    public void cleanExpiredPremiums() {
        List<Premium> expiredPremiums = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());
        if (expiredPremiums.size() > premiumBatchSize) {
            effectiveCleanExpiredPremiums(expiredPremiums);
        }
        premiumRepository.deleteAll(expiredPremiums);
    }

    private void effectiveCleanExpiredPremiums(List<Premium> expiredPremiums) {
        ExecutorService executorService = Executors.newCachedThreadPool();

    }
}
