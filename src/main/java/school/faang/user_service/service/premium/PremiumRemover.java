package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PremiumRemover {

    private final PremiumService premiumService;

    @Scheduled(cron = "${scheduler.premium-remover}")
    public void removePremium() {
        premiumService.deleteExpiredPremiums();
    }
}
