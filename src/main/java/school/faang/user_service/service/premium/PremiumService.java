package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.payment.Currency;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.PaymentFailedException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Service
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final UserRepository userRepository;
    private final PaymentServiceClient paymentServiceClient;

    @Transactional
    public Premium buyPremium(long userId, PremiumPeriod premiumPeriod) {
        if (premiumRepository.existsByUserId(userId)) {
            throw new IllegalStateException("The user with id " + userId + " already has a premium subscription.");
        }

        long paymentNumber = ThreadLocalRandom.current().nextLong();
        BigDecimal amount = BigDecimal.valueOf(premiumPeriod.getPrice());
        PaymentRequest paymentRequest = new PaymentRequest(paymentNumber, amount, Currency.USD);

        PaymentResponse paymentResponse = paymentServiceClient.sendPayment(paymentRequest);

        if (paymentResponse.status() != PaymentStatus.SUCCESS) {
            throw new PaymentFailedException(paymentResponse.message());
        }

        User user = userRepository.findById(userId).orElseThrow();

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime premiumEndDate = currentDateTime.plusDays(premiumPeriod.getDays());

        Premium premium = Premium.builder()
                .user(user)
                .startDate(currentDateTime)
                .endDate(premiumEndDate)
                .build();

        return premiumRepository.save(premium);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkPremiumExpiration() {
        LocalDateTime now = LocalDateTime.now();
        List<Premium> expiredPremiums = premiumRepository.findAllByEndDateBefore(now);

        // Можно к примеру отправить уведомление пользователю, об истёкшем премиуме
        expiredPremiums.forEach(premium ->
                premiumRepository.deleteById(premium.getId()));
    }
}
