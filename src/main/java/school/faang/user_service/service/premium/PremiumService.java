package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumService {
    @Value("${task.expired-premium-remove.batch-size}")
    private int batchSize;

    private final PremiumRepository premiumRepository;

    @Transactional(readOnly = true)
    public List<List<Long>> findExpiredPremiumIds() {
        LocalDateTime now = LocalDateTime.now();
        List<Long> premiumIDList = premiumRepository.findAllByEndDateBefore(now).stream()
                .map(Premium::getId).toList();
        return ListUtils.partition(premiumIDList, batchSize);
    }

    @Async("premiumRemoveTaskExecutor")
    public void deleteAsyncPremiumByIds(List<Long> ids) {
        log.info("Delete expired premiums in the Thread {}", Thread.currentThread().getName());
        premiumRepository.deleteAllById(ids);
    }
}
