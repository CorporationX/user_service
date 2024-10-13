package school.faang.user_service.service.premium;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.PremiumPeriod;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.payment.PaymentService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.premium.PremiumValidator;

import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class PremiumPurchaseService {

    private final PremiumRepository premiumRepository;
    private final PremiumValidator premiumValidator;
    private final PremiumMapper premiumMapper;
    private final PaymentService paymentService;
    private final UserService userService;

    @Transactional
    public PremiumDto buy(Long userId, PremiumPeriod premiumPeriod) {
        log.info("Attempting to purchase premium for userId: {} with period: {}", userId, premiumPeriod);

        premiumValidator.validatePremiumAlreadyExistsByUserId(userId);

        User user = userService.getUserById(userId);
        log.debug("User retrieved: {}", user);

        Premium premium = createPremium(user, premiumPeriod);
        log.debug("Premium created: {}", premium);

        Long paymentNumber = premiumRepository.getPremiumPaymentNumber();
        log.debug("Generated payment number: {}", paymentNumber);

        premiumRepository.save(premium);
        log.debug("Premium saved for userId: {} with paymentNumber: {}", userId, paymentNumber);

        paymentService.sendPayment(premiumPeriod.getPrice(), paymentNumber);
        log.info("Payment sent for userId: {} with amount: {}", userId, premiumPeriod.getPrice());

        return premiumMapper.toPremiumDto(premium);
    }

    private Premium createPremium(User user, PremiumPeriod premiumPeriod) {
        LocalDateTime now = LocalDateTime.now();

        return Premium.builder()
                .user(user)
                .startDate(now)
                .endDate(now.plusDays(premiumPeriod.getDays()))
                .build();
    }
}
