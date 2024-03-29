package school.faang.user_service.service.premium;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.controller.premium.PremiumPeriod;
import school.faang.user_service.dto.payment.Currency;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final UserRepository userRepository;
    private final PremiumMapper premiumMapper;

    @Transactional
    public PremiumDto buyPremium(long userId, PremiumPeriod premiumPeriod) {
        if (premiumRepository.existsByUserId(userId)) {
            throw new DataValidationException("The user"+userId+" is already has Premium subscription");
        }

        PaymentRequest paymentRequest = generatePaymentRequest(userId, premiumPeriod);

        ResponseEntity<PaymentResponse> paymentResponse = paymentServiceClient.sendPayment(paymentRequest);
        if (!paymentResponse.getStatusCode().equals(HttpStatus.OK)) {
            throw new DataValidationException("Transaction failed!");
        }
        PaymentResponse payment = paymentResponse.getBody();

        Premium premium = acqiurePremium(userId, premiumPeriod);

        return premiumMapper.toDto(premium);
    }

    private PaymentRequest generatePaymentRequest(Long userId, PremiumPeriod premiumPeriod) {
        return PaymentRequest.builder()
                .paymentNumber(userId)//how can also generate unique payment number??
                .amount(premiumPeriod.getCost())
                .currency(Currency.USD)
                .build();

    }

    private Premium acqiurePremium(Long userId, PremiumPeriod premiumPeriod) {
        User user = userRepository.findById(userId).get();

        Premium premium = Premium.builder()
                .user(user)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plus(premiumPeriod.getDays(), ChronoUnit.DAYS))
                .build();

        Premium premiumNew = premiumRepository.save(premium);
        user.setPremium(premiumNew);
        return premiumNew;
    }
}
