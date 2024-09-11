package school.faang.user_service.service.premium;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.payment.PaymentServiceClient;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.payment.PaymentStatus;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.payment.UnSuccessPaymentException;
import school.faang.user_service.exception.premium.PremiumCheckFailureException;
import school.faang.user_service.exception.premium.PremiumNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static school.faang.user_service.service.premium.util.PremiumErrorMessages.UNSUCCESSFUL_PAYMENT;
import static school.faang.user_service.service.premium.util.PremiumErrorMessages.USER_ALREADY_HAS_PREMIUM;
import static school.faang.user_service.service.premium.util.PremiumErrorMessages.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final UserRepository userRepository;
    private final PaymentServiceClient paymentServiceClient;

    @Transactional
    public Premium buyPremium(long userId, PremiumPeriod period) {
        log.info("User with id: {} buys a premium {} days subscription", userId, period.getDays());
        User user = checkUserForSubPeriod(userId);
        PaymentResponse paymentResponse = sendPayment(period);
        checkPaymentResponse(paymentResponse, userId, period);
        var premium = new Premium(null, user, LocalDateTime.now(), LocalDateTime.now().plusDays(period.getDays()));
        return premiumRepository.save(premium);
    }

    private PaymentResponse sendPayment(PremiumPeriod period) {
        var paymentRequest = PaymentRequest
                .builder()
                .paymentNumber(System.currentTimeMillis())
                .amount(BigDecimal.valueOf(period.getCost()))
                .currency(period.getCurrency())
                .build();
        return paymentServiceClient.sendPayment(paymentRequest);
    }

    private User checkUserForSubPeriod(long userId) {
        log.info("Verification of User with id: {} for premium subscription", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new PremiumNotFoundException(USER_NOT_FOUND, userId));
        Premium premium = user.getPremium();
        if (premium != null) {
            throw new PremiumCheckFailureException(USER_ALREADY_HAS_PREMIUM, premium.getEndDate());
        }
        return user;
    }

    private void checkPaymentResponse(PaymentResponse paymentResponse, long userId, PremiumPeriod period) {
        log.info("Check payment response: {}", paymentResponse);
        if (!paymentResponse.status().equals(PaymentStatus.SUCCESS)) {
            throw new UnSuccessPaymentException(UNSUCCESSFUL_PAYMENT, userId, period.getDays());
        }
    }
}
