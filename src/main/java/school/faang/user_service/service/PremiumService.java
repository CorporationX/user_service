package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Currency;
import school.faang.user_service.entity.premium.PaymentRequest;
import school.faang.user_service.entity.premium.PaymentResponse;
import school.faang.user_service.entity.premium.PaymentStatus;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.PremiumValidator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PremiumService {

    private final UserRepository userRepo;
    private final PaymentServiceClient paymentServiceClient;
    private final PremiumValidator premiumValidator;
    private final PremiumMapper mapper;

    public PremiumDto buyPremium(Long userId, PremiumPeriod period) {
        User user = userRepo.findById(userId).orElseThrow(()
                -> new IllegalArgumentException("User not found"));
        premiumValidator.validatePremium(userId);
        PaymentRequest paymentRequest = new PaymentRequest(
                new Random().nextLong(1000),
                BigDecimal.valueOf(period.getPrice()), Currency.USD);

        ResponseEntity<PaymentResponse> paymentResponseEntity =
                paymentServiceClient.sendPayment(paymentRequest);

        PaymentResponse response = paymentResponseEntity.getBody();
        if (response != null && response.status() == PaymentStatus.SUCCESS) {
            return createPremium(user, period);
        } else {
            throw new DataValidationException("Payment failed");
        }
    }

    public PremiumDto createPremium(User user, PremiumPeriod period) {
        Premium premium = new Premium();
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(period.getDays());

        premium.setUser(user);
        premium.setStartDate(startDate);
        premium.setEndDate(endDate);

        return mapper.toDto(premium);
    }
}
