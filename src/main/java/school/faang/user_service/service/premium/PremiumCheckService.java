package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.payment.PaymentStatus;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.payment.UnSuccessPaymentException;
import school.faang.user_service.exception.premium.PremiumCheckFailureException;
import school.faang.user_service.exception.premium.PremiumNotFoundException;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;

import static school.faang.user_service.service.premium.util.PremiumErrorMessages.UNSUCCESSFUL_PREMIUM_PAYMENT;
import static school.faang.user_service.service.premium.util.PremiumErrorMessages.USER_ALREADY_HAS_PREMIUM;
import static school.faang.user_service.service.premium.util.PremiumErrorMessages.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumCheckService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User checkUserForSubPeriod(long userId) {
        log.info("Verification of User with id: {} for premium subscription", userId);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new PremiumNotFoundException(USER_NOT_FOUND, userId));
        Premium premium = user.getPremium();
        if (premium != null && premium.getEndDate().isAfter(LocalDateTime.now())) {
            throw new PremiumCheckFailureException(USER_ALREADY_HAS_PREMIUM, premium.getEndDate());
        }
        return user;
    }

    public void checkPaymentResponse(PaymentResponse paymentResponse, long userId, PremiumPeriod period) {
        log.info("Check payment response: {}", paymentResponse);
        if (paymentResponse.status().equals(PaymentStatus.NOT_SUCCESS)) {
            throw new UnSuccessPaymentException(UNSUCCESSFUL_PREMIUM_PAYMENT, userId, period.getDays());
        }
    }
}
