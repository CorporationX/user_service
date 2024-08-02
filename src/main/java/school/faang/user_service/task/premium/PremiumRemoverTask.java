package school.faang.user_service.task.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.premium.PremiumService;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PremiumRemoverTask {

    private final PremiumService premiumService;

    @Scheduled(cron = "${task.expired-premium-remove.remove-premium-interval}")
    public void removePremium(){
        log.info("Start task : delete expired premiums {}", Thread.currentThread().getName());
        List<List<Long>> expiredPremiumIds = premiumService.findExpiredPremiumIds();
        expiredPremiumIds.forEach(premiumService::deleteAsyncPremiumByIds);
    }
}
