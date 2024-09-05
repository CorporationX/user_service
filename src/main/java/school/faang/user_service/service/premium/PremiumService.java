package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumService {

    private final PremiumRepository premiumRepository;

    @Async("executor")
    public CompletableFuture<Void> removePremium(int batchSize) {
        List<Premium> premiumForRemove = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());

        int totalSize = premiumForRemove.size();
        int numOfBatches = (totalSize + batchSize - 1) / batchSize;

        for (int i = 0; i < numOfBatches; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, totalSize);

            List<Premium> batch = premiumForRemove.subList(start, end);

            premiumRepository.deleteAll(batch);
            log.info("Sublist with elements from {} to {} deleted", start, end);
        }
        return CompletableFuture.completedFuture(null);
    }
}
