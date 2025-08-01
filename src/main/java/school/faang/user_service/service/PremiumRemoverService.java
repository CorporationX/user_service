package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.premium.PremiumService;

@Component
@RequiredArgsConstructor
public class PremiumRemoverService {

    private final PremiumService premiumService;

    @Value("${premium.remover.batchSize}")
    private int batchSize;

    @Scheduled(cron = "${premium.remover.cron}")
    private void removePremiumUsers() {
        premiumService.removeExpiredPremiums(batchSize);
    }
}
