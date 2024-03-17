package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.premium.PremiumRepository;

@Component
@RequiredArgsConstructor
public class PremiumValidator {

    private final PremiumRepository premiumRepo;

    public void validatePremiumExist(Long userId) {
        if (premiumRepo.existsById(userId)) {
            throw new DataValidationException("User already has a premium");
        }
    }
}
