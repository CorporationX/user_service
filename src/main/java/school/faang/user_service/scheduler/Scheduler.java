package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.premium.PremiumService;

@Component
@RequiredArgsConstructor
public class Scheduler {

    private final PremiumService premiumService;

    @Scheduled(cron = "${scheduler.clear-premiums}")
    public void deleteExpiredPremiums() {
        premiumService.deleteExpiredPremiums();
    }
}