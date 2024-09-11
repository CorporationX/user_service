package school.faang.user_service.service.premium;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.payment.PaymentServiceClient;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final PremiumCheckService premiumCheckService;

    @Transactional
    public Premium buyPremium(long userId, PremiumPeriod period) {
        log.info("User with id: {} buy a premium {} days subscription", userId, period.getDays());
        User user = premiumCheckService.checkUserForSubPeriod(userId);
        PaymentResponse paymentResponse = sendPayment(period);
        premiumCheckService.checkPaymentResponse(paymentResponse, userId, period);
        var premium = new Premium(null, user, LocalDateTime.now(), LocalDateTime.now().plusDays(period.getDays()));
        if (user.getPremium() != null) {
            premiumRepository.delete(user.getPremium());
        }
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
}
