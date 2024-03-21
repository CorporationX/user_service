package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.service.AsyncPremiumCleanupService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduledPremiumCleanupService {
    private final AsyncPremiumCleanupService asyncPremiumCleanupService;


    public void cleanExpiredPremiums() {
        log.info("Cleaning expired premiums");
        List<List<Premium>> expiredPremiumBatches = asyncPremiumCleanupService.findExpiredPremiums();
        expiredPremiumBatches.forEach(asyncPremiumCleanupService::cleanExpiredPremiumsAsync);
    }
}
