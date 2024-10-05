package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.premium.PremiumManagementService;

@Component
@RequiredArgsConstructor
public class PremiumRemover {

    private final PremiumManagementService premiumManagementService;

    @Scheduled(cron = "${premium.scheduled.daily}")
    public void removeExpiredPremiums() {
        premiumManagementService.removeExpiredPremiums();
    }
}
