package school.faang.user_service.service.premium;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.premium.PremiumNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.payment.PaymentService;
import school.faang.user_service.entity.premium.Premium;

import static school.faang.user_service.service.premium.util.PremiumBuilder.buildPremium;
import static school.faang.user_service.service.premium.util.PremiumErrorMessages.USER_NOT_FOUND_PREMIUM;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final PaymentService paymentService;
    private final PremiumValidationService premiumValidationService;
    private final UserRepository userRepository;

    @Transactional
    public Premium buyPremium(long userId, PremiumPeriod period) {
        log.info("User with id: {} buy a premium {} days subscription", userId, period.getDays());
        User user = userRepository.findById(userId).orElseThrow(() ->
                new PremiumNotFoundException(USER_NOT_FOUND_PREMIUM, userId));
        premiumValidationService.validateUserForSubPeriod(userId, user.getPremium());
        PaymentResponseDto paymentResponse = paymentService.sendPayment(period);
        premiumValidationService.checkPaymentResponse(paymentResponse, userId, period);

        Premium premium = buildPremium(user, period);
        if (user.getPremium() != null) {
            premiumRepository.delete(user.getPremium());
        }
        return premiumRepository.save(premium);
    }
}
