package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.PaymentRequest;
import school.faang.user_service.dto.PaymentResponse;
import school.faang.user_service.dto.PaymentStatus;
import school.faang.user_service.dto.PremiumActivated;
import school.faang.user_service.dto.PremiumRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.PaymentProceedException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.PaymentService;
import school.faang.user_service.service.UserService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final UserService userService;
    private final PaymentService paymentService;
    private final PremiumMapper premiumMapper;

    public PremiumActivated getPremiumForUserId(Long userId) {
        return premiumRepository.findByUserId(userId)
                .filter(premium -> premium.getEndDate().isAfter(LocalDateTime.now()))
                .map(premiumMapper::premiumToPremiumActivated)
                .orElse(null);
    }

    public PremiumActivated subscribeToPremium(PremiumRequest premiumRequest) {
        validatePremium(premiumRequest);

        payPremium(premiumRequest);

        Long userId = premiumRequest.userId();
        Long days = premiumRequest.daysCount();

        User user = getUserById(userId);
        LocalDateTime premiumStartDate = LocalDateTime.now();
        LocalDateTime premiumEndDate = premiumStartDate.plusDays(days);

        Premium premium = new Premium();
        premium.setUser(user);
        premium.setStartDate(premiumStartDate);
        premium.setEndDate(premiumEndDate);
        premiumRepository.save(premium);

        log.debug("Premium for user {} with start date {} and end date {} created",
                user.getUsername(), premiumStartDate, premiumEndDate);

        return premiumMapper.premiumToPremiumActivated(premium);
    }

    private User getUserById(long userId) {
        return userService.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с ID: " + userId + " не найден."));
    }

    private void validatePremium(PremiumRequest premiumRequest) {
        Long userId = premiumRequest.userId();

        if (premiumRepository.existsByUserIdAndEndDateAfter(userId, LocalDateTime.now())) {
            throw new DataValidationException("Премиум для текущего пользователя уже имеется.");
        }
    }

    private void payPremium(PremiumRequest premiumRequest) {
        PaymentRequest paymentRequest = new PaymentRequest(
                paymentService.getNextPaymentId(),
                premiumRequest.amount(),
                premiumRequest.currency()
        );

        PaymentResponse paymentResponse = paymentService.initPayment(paymentRequest);

        if (!paymentResponse.status().equals(PaymentStatus.SUCCESS)) {
            throw new PaymentProceedException("Ошибка платежа: " + paymentResponse.message());
        }

        log.debug("Подписка оплачена успешно. Код верификации: {}", paymentResponse.verificationCode());
    }
}
