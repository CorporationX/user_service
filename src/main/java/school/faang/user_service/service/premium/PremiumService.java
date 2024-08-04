package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumService {
    private static final int COUNT_THREAD = 100;
    private final PremiumRepository premiumRepository;

    @Transactional
    public void removingExpiredPremiumAccess(int batchSize) {
        ExecutorService executorService = Executors.newFixedThreadPool(COUNT_THREAD);

        List<Premium> premiumList = premiumRepository
                .findAllByEndDateBefore(LocalDateTime.now());

        if (premiumList.isEmpty()) {
            log.info("Список для премиум-пользователей пустой, некого удалять");
            return;
        }

        int count = (int) Math.ceil((double) premiumList.size() / batchSize);
        for (int i = 0; i < count; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, premiumList.size());
            List<Premium> batch = premiumList.subList(start, end);

            executorService.submit(() ->
                    premiumRepository.deleteAllById(batch.stream()
                            .map(Premium::getId)
                            .toList()));
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error(ExceptionMessages.CLOSURE_THREAD_EXCEPTION, e);
            throw new IllegalStateException(ExceptionMessages.CLOSURE_THREAD_EXCEPTION, e);
        }
    }
}
