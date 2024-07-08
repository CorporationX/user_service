package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.*;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.PaymentFailureException;
import school.faang.user_service.exception.PremiumAlreadyPurchasedException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final UserRepository userRepository;
    private final PremiumMapper premiumMapper;
    private final PaymentServiceClient paymentServiceClient;

    @Transactional
    public PremiumDto buyPremium(long userId, PremiumPeriod premiumPeriod) {
        if (premiumRepository.existsByUserId(userId)) {
            throw new PremiumAlreadyPurchasedException(userId);
        }
        PaymentResponse paymentResponse = paymentServiceClient.sendPaymentRequest(
            new PaymentRequest(
                0,
                new BigDecimal(premiumPeriod.getCost()),
                Currency.USD
            )
        );
        if (paymentResponse.status() != PaymentStatus.SUCCESS) {
            throw new PaymentFailureException(paymentResponse.paymentNumber());
        }
        LocalDateTime startDate = LocalDateTime.now();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        Premium premium = Premium.builder()
            .user(user)
            .startDate(startDate)
            .endDate(startDate.plusDays(premiumPeriod.getDays()))
            .build();
        premiumRepository.save(premium);
        return premiumMapper.toDto(premium);
    }
}
