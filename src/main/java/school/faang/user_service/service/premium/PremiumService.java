package school.faang.user_service.service.premium;

import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.payment_service.Currency;
import school.faang.user_service.client.payment_service.PaymentServiceClient;
import school.faang.user_service.client.payment_service.PaymentStatus;
import school.faang.user_service.client.payment_service.PaymentPostPayRequestDto;
import school.faang.user_service.client.payment_service.PaymentPostPayResponseDto;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PremiumService {

    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final PremiumMapper premiumMapper;

    @Transactional
    public PremiumDto buyPremium(@NonNull final Long userId, @NonNull final Integer days) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DataValidationException("User not found"));

        if (premiumRepository.existsByUserId(userId)) {
            throw new DataValidationException("User already has premium");
        }

        payForPremium(days);

        Premium premium = Premium.builder()
                .user(user)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(days))
                .build();

        return premiumMapper.toDto(premiumRepository.save(premium));
    }

    @Transactional
    public void removePremium(@NonNull final Long userId) {
        premiumRepository.deleteByUserId(userId);
    }

    private void payForPremium(final Integer days) {
        PremiumPeriod period = PremiumPeriod.getPremiumPeriod(days);
        PaymentPostPayRequestDto requestDto = new PaymentPostPayRequestDto(1, period.getPrice(), Currency.USD);
        ResponseEntity<PaymentPostPayResponseDto> response = paymentServiceClient.pay(requestDto);

        if (
                response.getStatusCode() != HttpStatus.OK ||
                response.getBody() == null ||
                response.getBody().status() != PaymentStatus.SUCCESS
        ) {
            throw new RuntimeException("Payment failed");
        }
    }
}
