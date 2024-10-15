package school.faang.user_service.validator.premium;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.premium.PremiumRepository;

@Component
@AllArgsConstructor
public class PremiumValidator {

    private final PremiumRepository premiumRepository;

    public void validatePremiumAlreadyExistsByUserId(Long userId) {
        if (premiumRepository.existsByUserId(userId)) {
            throw new DataValidationException("Premium already exists for userId: " + userId);
        }
    }
}
