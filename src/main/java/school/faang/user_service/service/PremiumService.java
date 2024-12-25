package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.payment.PaymentServiceClient;
import school.faang.user_service.dto.premium.PaymentRequestDto;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.model.PremiumPeriod;
import school.faang.user_service.model.premium.Premium;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.PaymentException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class PremiumService {
    private final PremiumRepository premRepo;
    private final UserService userService;
    private final PremiumMapper premMapper;
    private final PaymentServiceClient paymentServiceClient;

    public PremiumDto buyPremium(PremiumPeriod period, Long userId) {
        ensureUserHasNoPremium(userId);
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(period.getDays());

        Premium premium = Premium.builder()
                .user(userService.getUserById(userId))
                .startDate(startDate)
                .endDate(endDate)
                .build();

        PaymentRequestDto paymentRequest = PaymentRequestDto.builder()
                .userId(userId)
                .days(period.getDays())
                .build();

        if (paymentServiceClient.sendPaymentRequest(paymentRequest)) {
            premRepo.save(premium);
        } else {
            throw new PaymentException("Payment failed");
        }

        return premMapper.toDto(premium);
    }

    private void ensureUserHasNoPremium(Long userId) {
        if (premRepo.existsById(userId)) {
            throw new DataValidationException("User already has premium");
        }
    }
}
