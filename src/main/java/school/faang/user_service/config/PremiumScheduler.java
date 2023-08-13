package school.faang.user_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class PremiumScheduler {

    private final PremiumRepository premiumRepository;

    @Scheduled(cron = "0 0 0 1 * *")
    public void checkPremium() {
        StreamSupport.stream(premiumRepository.findAll().spliterator(), false)
                .forEach(premium -> {
                    if (premium.getEndDate().isBefore(LocalDateTime.now())) {
                        premiumRepository.delete(premium);
                    }
                });
    }
}
