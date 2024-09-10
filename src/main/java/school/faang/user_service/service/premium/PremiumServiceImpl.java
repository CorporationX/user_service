package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.premium.Currency;
import school.faang.user_service.dto.premium.PaymentRequest;
import school.faang.user_service.client.payment_service.PaymentServiceClient;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.validator.premium.PremiumValidator;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {

    private final PaymentServiceClient paymentServiceClient;
    private final PremiumRepository premiumRepository;
    private final UserRepository userRepository;
    private final PremiumValidator premiumValidator;
    private final PremiumMapper premiumMapper;

    @Override
    @Transactional
    public PremiumDto buyPremium(long userId, PremiumPeriod period) {
        premiumValidator.validateUser(userId);
        var user = findUserById(userId);
        var paymentRequestDto = buildRequest(period);
        var paymentResponseDto = paymentServiceClient.sendPayment(paymentRequestDto);
        premiumValidator.verifyPayment(paymentResponseDto);
        var premium = buildPremium(period, user);
        return premiumMapper.toDto(premiumRepository.save(premium));
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("User with ID: %d does not exist.".formatted(userId)));
    }

    private static PaymentRequest buildRequest(PremiumPeriod period) {
        return PaymentRequest.builder()
                .paymentNumber(System.currentTimeMillis())
                .amount(period.getPrice())
                .currency(Currency.USD)
                .build();
    }

    private static Premium buildPremium(PremiumPeriod period, User user) {
        return Premium.builder()
                .user(user)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(period.getDays()))
                .build();
    }
}