package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.paymentService.PaymentServiceClient;
import school.faang.user_service.client.paymentService.model.PaymentResponse;
import school.faang.user_service.client.paymentService.model.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.premium.PremiumIllegalArgumentException;
import school.faang.user_service.exception.premium.PremiumPaymentException;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.model.premium.PremiumPeriod;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.client.paymentService.model.PaymentRequest;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * @author Evgenii Malkov
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final PaymentServiceClient client;
    private final UserRepository userRepository;
    private final PremiumMapper mapper;
    @Value("${payment-service.currency:USD}")
    private String currency;

    @Transactional
    public PremiumDto buyPremium(long userId, PremiumPeriod period) {
        User user = findUserById(userId);
        boolean existPremium = premiumRepository.existsByUserId(userId);
        if (existPremium) {
            throw new PremiumIllegalArgumentException(
                    "User with id: " + userId + " already has a premium subscription");
        }
        Premium premium = savePremium(user, period.getDays());
        sendPayment(userId, period);
        return mapper.toPremiumDto(premium);
    }

    private void sendPayment(long userId, PremiumPeriod period) {
        Random random = new Random();
        long paymentNumber = random.nextLong(0, Long.MAX_VALUE);

        PaymentRequest request = new PaymentRequest(paymentNumber, period.getPrice(), currency);
        PaymentResponse response = client.sendPayment(request);
        if (!PaymentStatus.SUCCESS.equals(response.status())) {
            log.error("Error during payment execution. User: {}, paymentNumber: {}", userId, paymentNumber);
            throw new PremiumPaymentException("Error during payment execution");
        }
        log.info(response.message());
        savePaymentInfo(response);
    }

    private Premium savePremium(User user, int days) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = currentDateTime.plusDays(days);
        Premium premium = Premium.builder()
                .user(user)
                .startDate(currentDateTime)
                .endDate(endDateTime)
                .build();
        return premiumRepository.save(premium);
    }

    private void savePaymentInfo(PaymentResponse paymentResponse) {
        log.info("Сохранили необходимую инфу о платеже: {}...", paymentResponse.paymentNumber());
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new PremiumIllegalArgumentException(
                                "User with id: " + userId + " not found"));
    }
}
