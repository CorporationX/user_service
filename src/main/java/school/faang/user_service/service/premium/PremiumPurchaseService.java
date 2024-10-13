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

@Service
@AllArgsConstructor
public class PremiumPurchaseService {

    private final PremiumRepository premiumRepository;
    private final PremiumValidator premiumValidator;
    private final PremiumMapper premiumMapper;
    private final PaymentService paymentService;
    private final UserService userService;

    @Transactional
    public PremiumDto buy(Long userId, PremiumPeriod premiumPeriod) {
        premiumValidator.validatePremiumAlreadyExistsByUserId(userId);

        User user = userService.getUserById(userId);
        Premium premium = createPremium(user, premiumPeriod);
        Long paymentNumber = premiumRepository.getPremiumPaymentNumber();

        premiumRepository.save(premium);
        paymentService.sendPayment(premiumPeriod.getPrice(), paymentNumber);

        return premiumMapper.toDto(premium);
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
