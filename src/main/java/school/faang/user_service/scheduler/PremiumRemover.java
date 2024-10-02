package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.service.PremiumService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumRemover {

    @Value(value = "${spring.data.batch-size}")
    private int batchSize;

    private final PremiumService premiumService;

    @Scheduled(cron = "${spring.scheduler.cron.premium_removal}")
    public void removePremium() {
        List<Premium> premiumForRemove = premiumService.defineExpiredPremium();
        int totalSize = premiumForRemove.size();
        int numOfBatches = (totalSize + batchSize - 1) / batchSize;
        for (int i = 0; i < numOfBatches; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, totalSize);
            List<Premium> batch = premiumForRemove.subList(start, end);
            premiumService.removePremium(batch);
        }
    }
}