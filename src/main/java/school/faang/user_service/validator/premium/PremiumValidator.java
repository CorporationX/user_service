package school.faang.user_service.validator.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.premium.PremiumRepository;

@Component
@RequiredArgsConstructor
public class PremiumValidator {
    private final PremiumRepository premiumRepository;

    public void validate(long id) {
        if (premiumRepository.existsByUserId(id)) {
            throw new DataValidationException("Премиум подписка уже оформлена");
        }
    }
}
