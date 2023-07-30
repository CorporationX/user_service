package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.premium.PaymentResponse;
import school.faang.user_service.dto.premium.PaymentStatus;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.PaymentFailedException;
import school.faang.user_service.repository.premium.PremiumRepository;

@Component
@RequiredArgsConstructor
public class PremiumValidator {

    private final PremiumRepository premiumRepository;

    public void validateResponse(PaymentResponse response) {
        if (response == null || response.status() == null || !response.status().equals(PaymentStatus.SUCCESS)) {
            throw new PaymentFailedException("Payment failed, try again");
        }
    }

    public void validateExistPremiumFromUser(long userId) {
        if (premiumRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("User with %d already has a premium", userId));
        }
    }
}