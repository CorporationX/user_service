package school.faang.user_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.premium.PremiumService;

@Component
@Slf4j
@RequiredArgsConstructor
public class PremiumRenewalConfig {
    private final PremiumService premiumService;

    @Scheduled(cron = "${app.premium.cron}")
    public void PremiumRenewal() {
        premiumService.premiumRenewal();
    }
}
