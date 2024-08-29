package school.faang.user_service.scheduled.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.service.premium.PremiumService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PremiumRemover {
    private final PremiumService premiumService;
    @Value("${premium.removal.batchSize.size}")
    private int batchSize;

    @Scheduled(cron = "${premium.removal.scheduled.cron}")
    public void removePremium(){
        List<List<Premium>> allPremiumList = premiumService.removingExpiredPremiumAccess(batchSize);
        if(!allPremiumList.isEmpty()){
            allPremiumList.forEach(premiumService::executeAsyncBatchDelete);
        }
    }
}
