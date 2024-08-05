package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumService {
    private final PremiumRepository premiumRepository;

    @Transactional
    public void removingExpiredPremiumAccess(int batchSize) {
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

            executeAsyncBatchDelete(batch);
        }
    }

    @Async("asyncExecutor")
    public void executeAsyncBatchDelete(List<Premium> batch) {
        premiumRepository.deleteAllById(batch.stream()
                .map(Premium::getId)
                .toList());
    }
}
