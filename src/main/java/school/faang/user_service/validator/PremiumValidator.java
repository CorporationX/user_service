package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.premium.PremiumRepository;

@Component
@RequiredArgsConstructor
public class PremiumValidator {

    private final PremiumRepository premiumRepo;

    public void validatePremium(Long userId) {
        if (premiumRepo.existsByUserId(userId)) {
            throw new IllegalArgumentException("User already has a premium");
        }
    }
}
