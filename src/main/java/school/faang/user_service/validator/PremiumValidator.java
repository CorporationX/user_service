package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.AlreadyPurchasedException;
import school.faang.user_service.repository.premium.PremiumRepository;

@Component
@RequiredArgsConstructor
public class PremiumValidator {
    private final PremiumRepository premiumRepository;

    public void validateUserAlreadyHasPremium(long userId) {
        if (premiumRepository.existsByUserId(userId)) {
            throw new AlreadyPurchasedException(String.format("User with ID: %d already has a premium.", userId));
        }
    }
}
