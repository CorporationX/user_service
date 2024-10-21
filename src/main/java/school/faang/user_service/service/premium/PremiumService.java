package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.annotation.publisher.PublishEvent;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.premium.PremiumNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.payment.PaymentService;

import java.time.LocalDateTime;
import java.util.List;

import static school.faang.user_service.service.premium.util.PremiumErrorMessages.USER_NOT_FOUND_WHEN_BUYING_PREMIUM;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final PaymentService paymentService;
    private final PremiumValidationService premiumValidationService;
    private final UserRepository userRepository;

    @PublishEvent(returnedType = Premium.class)
    @Transactional
    public Premium buyPremium(long userId, PremiumPeriod period) {
        log.info("User with id: {} buy a premium {} days subscription", userId, period.getDays());
        User user = userRepository.findById(userId).orElseThrow(() ->
                new PremiumNotFoundException(USER_NOT_FOUND_WHEN_BUYING_PREMIUM, userId));
        premiumValidationService.validateUserForSubPeriod(userId, user);
        PaymentResponseDto paymentResponse = paymentService.sendPayment(period);
        premiumValidationService.checkPaymentResponse(paymentResponse, userId, period);
        Premium premium = Premium
                .builder()
                .user(user)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(period.getDays()))
                .build();
        return premiumRepository.save(premium);
    }

    @Transactional(readOnly = true)
    public List<Premium> findAllByEndDateBefore(LocalDateTime endDate) {
        return premiumRepository.findAllByEndDateBefore(endDate);
    }

    @Transactional
    public void deleteAllPremiumsById(List<Premium> premiums) {
        log.info("Delete all premiums");
        premiumRepository.deleteAllInBatch(premiums);
    }
}
