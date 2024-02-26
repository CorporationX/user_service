package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncPremiumCleanupService {
    private final PremiumRepository premiumRepository;

    @Async("premiumCleanupThreadPool")
    @Transactional
    public void cleanExpiredPremiumsAsync(List<Premium> premiums) {
        log.info("Cleaning async expired premiums, batch size: {}", premiums.size());
        premiumRepository.deleteAll(premiums);
    }
}
