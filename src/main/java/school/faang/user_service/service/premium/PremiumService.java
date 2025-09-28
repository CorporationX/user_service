package school.faang.user_service.service.premium;

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
import java.util.concurrent.Executors;

@Slf4j
@Service
public class PremiumService {

    private final PremiumRepository premiumRepository;
    private final int batchSize;

    public PremiumService(PremiumRepository premiumRepository,
                          @Value("${premium.remover.batch-size}") int batchSize) {
        this.premiumRepository = premiumRepository;
        this.batchSize = batchSize;
    }

    public void removeExpiredPremiums() {
        List<Premium> expired = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());

        if (expired.isEmpty()) {
            log.info(" No expired premium records found.");
            return;
        }

        int totalRecords = expired.size();
        int availableThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(availableThreads);

        log.info(
                " Found {} expired premium records. Starting removal in batches of {}, using {} threads...",
                totalRecords, batchSize, availableThreads
        );

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < totalRecords; i += batchSize) {
            int fromIndex = i;
            int toIndex = Math.min(i + batchSize, totalRecords);
            List<Premium> batch = expired.subList(fromIndex, toIndex);

            List<Long> userIds = batch.stream()
                    .map(premium -> premium.getUser().getId())
                    .limit(5)
                    .toList();

            log.info(
                    " Submitting batch: records {} to {} (example user IDs: {})",
                    fromIndex, toIndex - 1, userIds
            );

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    premiumRepository.deleteAll(batch);
                    log.debug(" Successfully deleted batch of size {}", batch.size());
                } catch (Exception exception) {
                    log.error(
                            " Error deleting batch [{} to {}]: {}",
                            fromIndex, toIndex - 1,
                            exception.getMessage(),
                            exception
                    );
                }
            }, executor);

            futures.add(future);
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            log.info(" All expired premium records deleted successfully.");
        } finally {
            executor.shutdown();
        }
    }
}
