package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumService {
    private final PremiumRepository premiumRepository;

    @Transactional(readOnly = true)
    public List<Premium> findAllByEndDateBefore(LocalDateTime endDate) {
        return premiumRepository.findAllByEndDateBefore(endDate);
    }

    @Async("premiumServicePool")
    @Transactional
    public void deleteAllPremiumsByIdAsync(List<Premium> premiums) {
        List<Long> premiumIds = premiums.stream()
                .map(Premium::getId)
                .toList();
        log.info("Delete all premiums by id: {}", premiumIds);
        premiumRepository.deleteByIdInBatch(premiumIds);
    }
}
