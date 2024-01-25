package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.UserService;

@Service
@RequiredArgsConstructor
public class PremiumValidator {
    private final UserService userService;
    private final PremiumRepository premiumRepository;
    public void validateResponseStatus(PaymentResponse paymentResponse) {
        if (paymentResponse.getStatus() == PaymentStatus.FAILURE) {
            throw new DataValidationException("Ошибка платежа");
        }
    }

    public void validateUserDoesNotHavePremium(long userId) {
        userService.validateUserDoesNotHavePremium(userId);
    }

}