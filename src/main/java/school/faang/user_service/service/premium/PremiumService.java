package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumService {

    private final PremiumRepository premiumRepository;

    @Value("${scheduler.premium.remover.batch-size:100}")
    private int batchSize;

    @Value("${scheduler.premium.remover.thread-pool-size:4}")
    private int threadPoolSize;

    @Transactional
    public void removeExpiredPremiums() {
        log.info("Starting premium removal process...");

        List<Premium> expiredPremiums = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());

        if (expiredPremiums.isEmpty()) {
            log.info("No expired premium records found");
            return;
        }

        log.info("Found {} expired premium records, processing in batches of {}...", expiredPremiums.size(), batchSize);

        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        for (int i = 0; i < expiredPremiums.size(); i += batchSize) {
            List<Premium> batch = expiredPremiums.subList(i, Math.min(i + batchSize, expiredPremiums.size()));
            executor.submit(() -> deleteBatch(batch));
        }
        executor.shutdown();
    }

    private void deleteBatch(List<Premium> batch) {
        try {
            log.info("Deleting batch of {} expired premiums...", batch.size());
            premiumRepository.deleteAllInBatch(batch);
        } catch (Exception e) {
            log.error("Error while deleting batch", e);
        }
    }
}
