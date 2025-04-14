package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PremiumRemover {

    private final PremiumService premiumService;

    @Scheduled(cron = "${scheduler.premium-remover}")
    public void removePremium() {
        log.info("Starting the task to delete expired premium subscriptions");
        premiumService.removeExpiredPremiums();
    }
}
