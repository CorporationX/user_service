package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.premium.Currency;
import school.faang.user_service.dto.premium.PaymentRequest;
import school.faang.user_service.dto.premium.PaymentResponse;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.ExistingPurchaseException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final UserRepository userRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final PremiumMapper premiumMapper;

    @Transactional
    public PremiumDto buyPremium(long userId, PremiumPeriod premiumPeriod) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (premiumRepository.existsByUserId(userId)) {
            throw new ExistingPurchaseException("User already has an active premium subscription");
        }

        payPremium(premiumPeriod);

        Premium savedPremium = savePremium(premiumPeriod, user);
        return premiumMapper.toPremiumDto(savedPremium);
    }

    private void payPremium(PremiumPeriod premiumPeriod) {
        long paymentNumber = premiumRepository.getPremiumPaymentNumber();
        PaymentRequest paymentRequest =
                new PaymentRequest(paymentNumber, new BigDecimal(premiumPeriod.getPrice()), Currency.USD);
        ResponseEntity<PaymentResponse> paymentResponseEntity = paymentServiceClient.sendPayment(paymentRequest);
        PaymentResponse response = paymentResponseEntity.getBody();

        if (response != null) {
            log.info("Payment response received: {}", response.message());
        } else {
            log.warn("No response from payment service.");
        }
    }

    private Premium savePremium(PremiumPeriod premiumPeriod, User user) {
        Premium premium = new Premium();
        premium.setUser(user);
        LocalDateTime startDate = LocalDateTime.now();
        premium.setStartDate(startDate);
        LocalDateTime endDate = startDate.plusDays(premiumPeriod.getDays());
        premium.setEndDate(endDate);
        return premiumRepository.save(premium);
    }
}
