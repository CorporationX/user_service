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

    @Scheduled(cron = "${premium.remover.cron}")
    public void removePremium() {
        log.info(" Scheduled premium removal started.");
        try {
            premiumService.removeExpiredPremiums();
            log.info(" Scheduled premium removal completed.");
        } catch (Exception exception) {
            log.error(" Error during scheduled premium removal: {}", exception.getMessage(), exception);
        }
    }
}
