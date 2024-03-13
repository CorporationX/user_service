package school.faang.user_service.validation.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.client.PaymentResponse;
import school.faang.user_service.dto.client.PaymentStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.FailedServiceInteractionException;
import school.faang.user_service.repository.UserRepository;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PremiumValidator {

    private final UserRepository userRepository;

    public void validatePaymentResponse(PaymentResponse response) {
        if (!PaymentStatus.SUCCESS.equals(response.getStatus())) {
            throw new FailedServiceInteractionException("Failed payment response");
        }
    }

    public void validateUserPremiumStatus(Long userId) {
        if (isUserHavePremium(userId)) {
            throw new DataValidationException("User already have premium");
        }
    }

    private boolean isUserHavePremium(Long userId) {
        HashSet<Long> premUserIds = userRepository.findPremiumUsers()
                .map(User::getId)
                .collect(Collectors.toCollection(HashSet::new));
        return premUserIds.contains(userId);
    }
}
