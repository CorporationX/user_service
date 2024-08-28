package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumService {
    private final PremiumRepository premiumRepository;

    @Transactional
    public List<List<Premium>> removingExpiredPremiumAccess(int batchSize) {
        List<Premium> premiumList = premiumRepository
                .findAllByEndDateBefore(LocalDateTime.now());

        if (premiumList.isEmpty()) {
            log.info("Список для премиум-пользователей пустой, некого удалять");
            return Collections.emptyList();
        }
        return ListUtils.partition(premiumList, batchSize);
    }

    @Async("taskExecutor")
    public void executeAsyncBatchDelete(List<Premium> batch) {
        premiumRepository.deleteAllById(batch.stream()
                .map(Premium::getId)
                .toList());
    }
}