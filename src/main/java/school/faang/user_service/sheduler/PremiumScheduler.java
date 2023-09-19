package school.faang.user_service.sheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.PremiumService;
import school.faang.user_service.service.UserRatingService;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PremiumScheduler {

    private final PremiumService premiumService;
    private final UserRatingService userRatingService;

    @Scheduled(cron = "${premium.scheduler_cron}")
    public void checkPremium() {
        premiumService.findAllPremium().forEach(premium -> {
            if (premium.getEndDate().isBefore(LocalDateTime.now())) {
                premiumService.delete(premium);
                userRatingService.depriveRatingEndPremium(premium.getUser().getId());
            }
        });
    }
}
