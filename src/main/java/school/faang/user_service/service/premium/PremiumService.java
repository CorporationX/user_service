package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final UserRepository userRepository;
    private final PaymentServiceClient paymentServiceClient;

    @Transactional
    public Premium buyPremium(long userId, PremiumPeriod premiumPeriod) {

        User user = userRepository.findById(userId).orElseThrow();
        if (premiumRepository.existsByUserId(user.getId())) {
            throw new IllegalStateException("The user with id " + userId + " already has a premium subscription.");
        }

        makePayment(premiumPeriod);

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime premiumEndDate = currentDateTime.plusDays(premiumPeriod.getDays());

        Premium premium = Premium.builder()
                .user(user)
                .startDate(currentDateTime)
                .endDate(premiumEndDate)
                .build();

        return premiumRepository.save(premium);
    }

    private void makePayment(PremiumPeriod premiumPeriod) {
        long paymentNumber = UUID.randomUUID().getLeastSignificantBits();
        BigDecimal amount = BigDecimal.valueOf(premiumPeriod.getPrice());
        PaymentRequest paymentRequest = new PaymentRequest(paymentNumber, amount, Currency.USD);

        ResponseEntity<PaymentResponse> paymentResponse = paymentServiceClient.sendPayment(paymentRequest);

        if (paymentResponse.getStatusCode() != HttpStatus.OK) {
            String message = Objects.requireNonNull(paymentResponse.getBody()).message();
            throw new PaymentFailedException(message);
        }
    }
}
