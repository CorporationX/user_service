package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.PremiumBoughtEvent;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.*;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.publisher.PremiumBoughtEventPublisher;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.PremiumValidator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PremiumService {

    private final PremiumRepository premiumRepo;
    private final PaymentServiceClient paymentServiceClient;
    private final PremiumValidator premiumValidator;
    private final PremiumMapper mapper;
    private final UserService userService;
    private final PremiumBoughtEventPublisher premiumBoughtEventPublisher;

    @Transactional
    public PremiumDto buyPremium(Long userId, PremiumPeriod period) {
        User user = userService.getUserById(userId);
        premiumValidator.validatePremiumExist(userId);
        PaymentRequest paymentRequest = new PaymentRequest(
                10L, BigDecimal.valueOf(period.getPrice()), Currency.USD);

        ResponseEntity<PaymentResponse> paymentResponseEntity =
                paymentServiceClient.sendPayment(paymentRequest);

        PaymentResponse response = paymentResponseEntity.getBody();
        if (response != null && response.status() == PaymentStatus.SUCCESS) {
            PremiumDto savedPremium = savePremium(user, period);
            publishPremiumBought(userId, period);
            return savedPremium;
        } else {
            throw new DataValidationException("Payment failed");
        }
    }

    private void publishPremiumBought(long userId, PremiumPeriod period) {
        PremiumBoughtEvent eventDto = PremiumBoughtEvent.builder()
                .receiverId(userId)
                .amountPayment(period.getPrice())
                .daysSubscription(period.getDays())
                .receivedAt(LocalDateTime.now())
                .build();

        premiumBoughtEventPublisher.publish(eventDto);
    }

    public PremiumDto savePremium(User user, PremiumPeriod period) {
        Premium premium = new Premium();
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(period.getDays());

        premium.setUser(user);
        premium.setStartDate(startDate);
        premium.setEndDate(endDate);

        premiumRepo.save(premium);

        return mapper.toDto(premium);
    }
}
