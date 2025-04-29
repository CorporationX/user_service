package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.premium.PremiumService;

@Slf4j
@Component
@RequiredArgsConstructor
public class PremiumRemover {

    private final PremiumService premiumService;

    @Scheduled(cron = "${scheduler.premium.remover.cron}")
    public void removePremium() {
        log.info("Running scheduled premium removal");
        premiumService.removeExpiredPremiums();
    }
}
