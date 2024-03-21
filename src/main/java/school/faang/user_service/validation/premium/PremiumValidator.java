package school.faang.user_service.validation.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ServiceInteractionException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

@Component
@RequiredArgsConstructor
public class PremiumValidator {

    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;

    public void validatePaymentResponse(PaymentResponse response) {
        if (!PaymentStatus.SUCCESS.equals(response.getStatus())) {
            throw new ServiceInteractionException("Failed payment response");
        }
    }

    public void validateUserPremiumStatus(Long userId) {
        if (isUserPremium(userId)) {
            throw new DataValidationException("User already has premium");
        }
    }

    private boolean isUserPremium(Long userId) {
        return premiumRepository.existsByUserId(userId);
    }
}
