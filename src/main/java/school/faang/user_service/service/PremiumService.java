package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.paymentService.PaymentServiceClient;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.premium.PremiumIllegalArgumentException;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.model.premium.PremiumPeriod;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

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
    private final PremiumMapper mapper;

    @Transactional
    public long buyPremium(long userId, PremiumPeriod period) {
        checkIfExistsUser(userId);
        boolean existPremium = premiumRepository.existsByUserId(userId);
        if (existPremium) {
            throw new PremiumIllegalArgumentException(
                    "User with id: " + userId + " already has a premium subscription");
        }
        return paymentClient.sendPaymentRequest(userId, period.getPrice());
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

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new PremiumIllegalArgumentException(
                                "User with id: " + userId + " not found"));
    }

    private void checkIfExistsUser(long userId) {
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            throw new PremiumIllegalArgumentException("User with id: " + userId + " not found");
        }
    }
}
