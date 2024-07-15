package school.faang.user_service.service.expiration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PremiumExpirationService implements ExpirationService {
    private final PremiumRepository premiumRepository;

    @Override
    public void checkExpirations(LocalDateTime currentDate) {
        List<Premium> expiredPremiums = premiumRepository.findAllByEndDateBefore(currentDate);
        premiumRepository.deleteAll(expiredPremiums);
    }
}
