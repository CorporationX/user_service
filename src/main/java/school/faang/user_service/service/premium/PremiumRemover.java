package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PremiumRemover {

    private final PremiumService premiumService;

    @Scheduled(cron = "${scheduler.clear-premiums}")
    public void removePremium() {
        premiumService.deleteExpiredPremiums();
    }
}
