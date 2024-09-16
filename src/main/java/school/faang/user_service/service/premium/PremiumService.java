package school.faang.user_service.service.premium;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.payment.PaymentService;
import school.faang.user_service.service.premium.util.Premium;
import school.faang.user_service.service.premium.util.PremiumBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final PaymentService paymentService;
    private final PremiumCheckService premiumCheckService;
    private final PremiumBuilder premiumBuilder;

    @Transactional
    public Premium buyPremium(long userId, PremiumPeriod period) {
        log.info("User with id: {} buy a premium {} days subscription", userId, period.getDays());
        User user = premiumCheckService.checkUserForSubPeriod(userId);
        PaymentResponse paymentResponse = paymentService.sendPayment(period);
        premiumCheckService.checkPaymentResponse(paymentResponse, userId, period);
        Premium premium = premiumBuilder.getPremium(user, period);
        if (user.getPremium() != null) {
            premiumRepository.delete(user.getPremium());
        }
        return premiumRepository.save(premium);
    }
}
