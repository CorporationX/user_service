package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import school.faang.user_service.service.user.premium.PremiumService;

@Service
@RequiredArgsConstructor
public class ScheduledPremiumStatusRemover {

    private final PremiumService premiumService;

    @Async
    @Scheduled(cron = "${premium.scheduler.expired-premium-remover.cron}")
    public void removeExpiredPremiumStatus() {
        premiumService.deleteAllExpiredPremium();
    }
}
