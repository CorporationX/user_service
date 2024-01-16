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

    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Moscow")
    public void cleanExpiredPremiums() {
        List<Premium> expiredPremiums = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());
        premiumRepository.deleteAll(expiredPremiums);
        System.out.println("Истекшие премиумы удалены");
    }
}
