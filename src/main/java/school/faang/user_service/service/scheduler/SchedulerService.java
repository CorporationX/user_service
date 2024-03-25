package school.faang.user_service.service.scheduler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final PremiumRepository premiumRepository;
    @Scheduled(cron = "${cleanUpPremiumRepo.cron}")
    @Transactional
    public void deleteExpiredPremium() {
        premiumRepository.deleteAllByEndDateBefore(LocalDateTime.now());
    }

}
