package school.faang.user_service.validation.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ServiceInteractionException;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class PremiumValidator {

    private final UserRepository userRepository;

    public void validatePaymentResponse(PaymentResponse response) {
        if (!PaymentStatus.SUCCESS.equals(response.getStatus())) {
            throw new ServiceInteractionException("Failed payment response");
        }
    }

    public void validateUserPremiumStatus(Long userId) {
        if (isUserPremium(userId)) {
            throw new DataValidationException("User already have premium");
        }
    }

    private boolean isUserPremium(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new EntityNotFoundException("User doesn't exist"));
        return user.getPremium() == null;
    }
}
