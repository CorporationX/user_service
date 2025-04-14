package school.faang.user_service.service.premium;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumServiceImpl implements PremiumService {
    public static final int DEFAULT_BATCH_SIZE = 100;

    private final PremiumRepository premiumRepository;
    private final ExecutorService executorService;
    private final BatchDeletionService batchDeletionService;

    @Value("${premium.expired.delete-batch:100}")
    private Integer batchSize;

    @Override
    public void removeExpiredPremiums() {
        if (batchSize == null || batchSize <= 0) {
            log.warn("Invalid batch size: {}. Using default value: 100", batchSize);
            batchSize = DEFAULT_BATCH_SIZE;
        }

        log.info("Starting removal of expired premiums...");
        List<Premium> expiredPremiums = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());

        if (expiredPremiums.isEmpty()) {
            log.info("No expired premiums found.");
            return;
        }

        List<List<Premium>> batches = Lists.partition(expiredPremiums, batchSize);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        log.info("Processing {} expired premiums in {} batches...", expiredPremiums.size(), batches.size());

        for (List<Premium> batch : batches) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        batchDeletionService.deleteBatch(batch);
                    }, executorService)
                    .exceptionally(ex -> {
                        log.error("Failed to delete batch: {}", ex.getMessage(), ex);
                        return null;
                    });
            futures.add(future);
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            log.info("Successfully removed {} expired premiums.", expiredPremiums.size());
        } catch (Exception ex) {
            log.error("Error while waiting for batch deletions to complete: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to remove expired premiums", ex);
        }
    }

    @Override
    public void deleteBatch(List<Premium> batch) {
    }
}