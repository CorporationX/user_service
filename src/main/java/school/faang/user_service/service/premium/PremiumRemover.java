package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumRemover {

    @Value(value = "${spring.data.batch-size}")
    private int batchSize;

    private final PremiumService premiumService;

    @Scheduled(cron = "0 0 3 * * MON")
    public void removePremium() {

        premiumService.removePremium(batchSize);
    }
}