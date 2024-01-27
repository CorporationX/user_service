package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.entity.premium.Premium;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PremiumCleanupService {
    private final PremiumRepository premiumRepository;

    @Scheduled(cron = "${premium_cleanup.time}", zone = "${premium_cleanup.zone}")
    public void cleanExpiredPremiums() {
        List<Premium> expiredPremiums = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());
        premiumRepository.deleteAll(expiredPremiums);
    }
}
