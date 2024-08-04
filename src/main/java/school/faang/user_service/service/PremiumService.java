package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.paymentService.PaymentServiceClient;
import school.faang.user_service.client.paymentService.model.PaymentRequest;
import school.faang.user_service.client.paymentService.model.PaymentStatus;
import school.faang.user_service.client.paymentService.model.Product;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.promotion.PremiumPaymentRequest;
import school.faang.user_service.exception.premium.PremiumIllegalArgumentException;
import school.faang.user_service.exception.promotion.PromotionIllegalArgumentException;
import school.faang.user_service.model.premium.PremiumPeriod;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.repository.promotion.PremiumPaymentRequestRepository;

import java.time.LocalDateTime;

/**
 * @author Evgenii Malkov
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final PaymentServiceClient paymentClient;
    private final UserRepository userRepository;
    private final PremiumPaymentRequestRepository paymentRequestRepository;

    @Transactional
    public PaymentRequest buyPremium(long userId, PremiumPeriod period) {
        User user = findUserById(userId);

        boolean existPremium = premiumRepository.existsByUserId(userId);
        if (existPremium) {
            throw new PremiumIllegalArgumentException(
                    "User with id: " + userId + " already has a premium subscription");
        }
        PremiumPaymentRequest request = paymentRequestRepository.save(
                PremiumPaymentRequest.builder()
                        .days(period.getDays())
                        .status(PaymentStatus.CREATED.toString())
                        .userId(user.getId())
                        .build());

        return paymentClient.sendPaymentRequest(request.getId().toString(),
                period.getPrice(), Product.PREMIUM);
    }

    @Transactional
    public void savePremium(long userId, int days) {
        User user = findUserById(userId);
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = currentDateTime.plusDays(days);
        Premium premium = Premium.builder()
                .user(user)
                .startDate(currentDateTime)
                .endDate(endDateTime)
                .build();
        premiumRepository.save(premium);
        log.info("Saved premium userId: {}, days: {}", userId, days);
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new PromotionIllegalArgumentException("User not found id: " + userId));
    }
}
