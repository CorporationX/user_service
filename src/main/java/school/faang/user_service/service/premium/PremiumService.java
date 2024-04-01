package school.faang.user_service.service.premium;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.email.EmailEvent;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.PaymentProcessingException;
import school.faang.user_service.integration.payment.PaymentService;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.publisher.EmailEventPublisher;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static school.faang.user_service.entity.premium.PremiumPeriod.fromDays;

@Service
@RequiredArgsConstructor
public class PremiumService {

    private final UserService userService;
    private final PremiumRepository premiumRepository;
    private final UserMapper userMapper;
    private final PaymentService paymentService;
    private final PremiumMapper premiumMapper;
    private final EmailEventPublisher emailEventPublisher;

    @Transactional
    public PremiumDto buyPremiumSubscription(Long userId, int days) {
        User user = userMapper.toUser(userService.getUserById(userId));
        checkPremiumStatusUser(userId);
        PremiumPeriod premiumPeriod = fromDays(days);
        paymentService.makePayment(premiumPeriod);
        PremiumDto premiumDto = premiumMapper.toDto(saveUserPremium(premiumPeriod, user));
        emailEventPublisher.publish(new EmailEvent(userId, LocalDateTime.now()));
        return premiumDto;
    }

    @Transactional
    public List<Premium> getExpiredPremiumSubscriptions() {
        return premiumRepository.findAllByEndDateBefore(LocalDateTime.now());
    }

    @Transactional
    public void deletePremiumSubscribe(Long premiumId) {
        premiumRepository.deleteById(premiumId);

    }

    private Premium saveUserPremium(PremiumPeriod premiumPeriod, User user) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(premiumPeriod.getDays());
        Premium premium = Premium.builder()
                .user(user)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        return premiumRepository.save(premium);
    }

    private void checkPremiumStatusUser(Long userId) {
        if (premiumRepository.existsByUserId(userId)) {
            throw new PaymentProcessingException("User by id: " + userId + " already has a premium subscription");
        }
    }
}
