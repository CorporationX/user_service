package school.faang.user_service.scheduler.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.service.premium.PremiumService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PremiumScheduler {

    private final PremiumService premiumService;

    @Scheduled(cron = "${scheduling.premium.cron.expression}")
    public void checkExpiredPremiumSubscription() {
        List<Premium> premiums = premiumService.getExpiredPremiumSubscriptions();
        premiums.forEach(premium -> {
            premiumService.deletePremiumSubscribe(premium.getId());
        });
    }
}
