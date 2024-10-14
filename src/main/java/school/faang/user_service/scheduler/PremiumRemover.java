package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.premium.PremiumManagementService;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PremiumRemover {

    private final PremiumManagementService premiumManagementService;

    @Scheduled(cron = "${premium.schedule}")
    public void removeExpiredPremiums() {
        log.info("{} Started the removal of expired premiums", LocalDateTime.now());
        premiumManagementService.removeExpiredPremiums();
    }
}
