package school.faang.user_service.scheduled.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.premium.PremiumService;

@Component
@RequiredArgsConstructor
public class PremiumRemover {
    private final PremiumService premiumService;

    @Scheduled(cron = "${premium.removal.scheduled.cron}")
    public void removePremium(){
        premiumService.removingExpiredPremiumAccess();
    }
}
