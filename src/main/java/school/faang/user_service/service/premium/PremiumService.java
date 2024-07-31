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

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumService {
    private static final int COUNT_THREAD = 100;
    private final PremiumRepository premiumRepository;
    @Value("${premium.removal.batchSize.size}")
    private int batchSize;

    @Transactional
    public void removingExpiredPremiumAccess() {
        ExecutorService executorService = Executors.newFixedThreadPool(COUNT_THREAD);
        List<Premium> premiumList = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());
        if (premiumList.isEmpty()) {
            log.info("список пуст, некого удалять");
        }
        int count = (int) Math.ceil((double) premiumList.size() / batchSize);
        for (int i = 0; i < count; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, premiumList.size());
            List<Premium> batch = premiumList.subList(start, end);

            executorService.submit(() -> batch.forEach(premiumRepository::delete));
        }
        executorService.shutdown();
    }
}
