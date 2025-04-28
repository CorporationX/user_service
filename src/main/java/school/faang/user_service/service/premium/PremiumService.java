package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.PaymentRequestDto;
import school.faang.user_service.dto.PaymentResponseDto;
import school.faang.user_service.dto.PaymentStatus;
import school.faang.user_service.dto.PremiumActivatedDto;
import school.faang.user_service.dto.PremiumRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.PaymentProceedException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.PaymentService;
import school.faang.user_service.service.UserServiceImpl;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final UserServiceImpl userService;
    private final PaymentService paymentService;
    private final PremiumMapper premiumMapper;

    public ResponseEntity<PremiumActivatedDto> getPremiumForUserId(Long userId) {
        return premiumRepository.findByUserId(userId)
                .filter(premium -> premium.getEndDate().isAfter(LocalDateTime.now()))
                .map(premiumMapper::premiumToPremiumActivated)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<PremiumActivatedDto> subscribeToPremium(PremiumRequestDto premiumRequestDto) {
        validatePremium(premiumRequestDto);

        User user = getUserById(premiumRequestDto.userId());

        payPremium(premiumRequestDto);

        LocalDateTime premiumStartDate = LocalDateTime.now();
        LocalDateTime premiumEndDate = premiumStartDate.plusDays(premiumRequestDto.daysCount());

        Premium premium = new Premium();
        premium.setUser(user);
        premium.setStartDate(premiumStartDate);
        premium.setEndDate(premiumEndDate);
        premiumRepository.save(premium);

        log.debug("Premium for user {} with start date {} and end date {} created",
                user.getUsername(), premiumStartDate, premiumEndDate);

        return ResponseEntity.ok(premiumMapper.premiumToPremiumActivated(premium));
    }

    private User getUserById(long userId) {
        return userService.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID: " + userId + " not found"));
    }

    private void validatePremium(PremiumRequestDto premiumRequestDto) {
        Long userId = premiumRequestDto.userId();

        if (premiumRepository.existsByUserIdAndEndDateAfter(userId, LocalDateTime.now())) {
            throw new DataValidationException("Premium already exists");
        }
    }

    private void payPremium(PremiumRequestDto premiumRequestDto) {
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto(
                paymentService.getNextPaymentId(),
                premiumRequestDto.amount(),
                premiumRequestDto.currency()
        );

        PaymentResponseDto paymentResponseDto = paymentService.initPayment(paymentRequestDto);

        if (!paymentResponseDto.status().equals(PaymentStatus.SUCCESS)) {
            throw new PaymentProceedException("Payment failed: " + paymentResponseDto.message());
        }

        log.debug("Subscription paid successfully. Verification code: {}", paymentResponseDto.verificationCode());
    }
}
