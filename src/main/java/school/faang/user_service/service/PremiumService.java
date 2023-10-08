package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.dto.payment.Currency;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.PaymentFailedException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.user.UserService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final PremiumMapper premiumMapper;
    private final UserService userService;
    private final PaymentServiceClient paymentServiceClient;
    private final Random random;
    @Value("${spring.premium.pay_for_day}")
    private double payForDay;

    @Transactional
    public PremiumDto buyPremium(Long userId, PremiumPeriod period) {
        if (premiumRepository.existsByUserId(userId)) {
            throw new DataValidationException("User with id " + userId + " already has premium");
        }
        PaymentRequest request = generatePaymentRequest(period);
        ResponseEntity<PaymentResponse> response = paymentServiceClient.sendPayment(request);

        validateResponse(response);

        User user = userService.getUser(userId);
        Premium premium = Premium.builder()
                .user(user)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(period.getDays()))
                .build();
        premium = premiumRepository.save(premium);

        log.info("Successfully bought premium for user with id {}", userId);
        return premiumMapper.toDto(premium);
    }

    private void validateResponse(ResponseEntity<PaymentResponse> responseEntity) {
        PaymentResponse paymentResponse = responseEntity.getBody();
        if (!responseEntity.getStatusCode().is2xxSuccessful() ||
                !Objects.requireNonNull(paymentResponse).getStatus().equals(PaymentStatus.SUCCESS)) {
            throw new PaymentFailedException("Payment failed. Please try again");
        }
    }

    private PaymentRequest generatePaymentRequest(PremiumPeriod period) {
        return PaymentRequest.builder()
                .paymentNumber(random.nextLong(10000L, 99999L)) // generates random payment number
                .amount(BigDecimal.valueOf(period.getDays() * payForDay)) // calculates amount
                .currency(Currency.USD) // default currency
                .build();
    }
}
