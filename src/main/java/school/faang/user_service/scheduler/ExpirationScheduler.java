package school.faang.user_service.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.expiration.ExpirationService;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ExpirationScheduler {
    List<ExpirationService> expirationServices;

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkExpirations() {
        LocalDateTime currentDate = LocalDateTime.now();
        expirationServices.forEach(expirationService -> expirationService.processExpiredItems(currentDate));
    }
}
