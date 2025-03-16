package school.faang.user_service.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class PremiumExpiredScheduleTask {
    private final long cronDelayInMillis = 60000;
    private final PremiumRepository premiumRepository;
    private final AtomicBoolean premiumExpiredScheduleTaskRunning = new AtomicBoolean(false);

    @Scheduled(fixedDelay = cronDelayInMillis)
    public void searchAndDeleteExpiredPremiums() {
        if (premiumExpiredScheduleTaskRunning.compareAndSet(false, true)) {
            try {
                deleteExpiredPremiums();
            } finally {
                premiumExpiredScheduleTaskRunning.set(false);
            }
        } else {
            log.debug("Премиум подписки всё еще удаляются");
        }
    }

    private void deleteExpiredPremiums() {
        premiumRepository.findAllByEndDateBefore(LocalDateTime.now())
                .forEach(premium -> {
                    String premiumUserName = premium.getUser().getUsername();
                    premiumRepository.delete(premium);
                    log.debug("Премиум для пользователя [{}] удален", premiumUserName);
                });
    }
}
