package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumService {

    private final PremiumRepository premiumRepository;

    public List<Premium> defineExpiredPremium() {
        return premiumRepository.findAllByEndDateBefore(LocalDateTime.now());
    }

    @Async("executor")
    public void removePremium(List<Premium> batch) {
        premiumRepository.deleteAll(batch);
        log.info("Sublist with elements from {} to {} deleted", batch.get(0), batch.get(batch.size() - 1));
    }
}
