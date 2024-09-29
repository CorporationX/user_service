package school.faang.user_service.service.premium;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.client.Currency;
import school.faang.user_service.dto.client.PaymentRequest;
import school.faang.user_service.dto.client.PaymentResponse;
import school.faang.user_service.dto.client.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.dto.premium.PremiumPeriod;
import school.faang.user_service.exception.PaymentFailureException;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.validator.premium.PremiumValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class PremiumService {

    private final PremiumRepository premiumRepository;
    private final PremiumValidator premiumValidator;
    private final PremiumMapper premiumMapper;
    private final PaymentServiceClient paymentServiceClient;
    private final UserValidator userValidator;

    @Transactional
    public PremiumDto buy(Long userId, PremiumPeriod premiumPeriod) {
        userValidator.validateUserIsExisted(userId);
        premiumValidator.validatePremiumAlreadyExistsByUserId(userId);

        PremiumDto premiumDto = createPremium(userId, premiumPeriod);
        sendPayment(premiumPeriod, userId);
        premiumRepository.save(premiumMapper.toEntity(premiumDto));

        return premiumDto;
    }

    private void sendPayment(PremiumPeriod premiumPeriod, Long userId) {
        Long paymentNumber = premiumRepository.getPremiumPaymentNumber();
        PaymentRequest request = new PaymentRequest(
                paymentNumber,
                new BigDecimal(premiumPeriod.getPrice()),
                Currency.USD
        );
        ResponseEntity<PaymentResponse> response = paymentServiceClient.sendPayment(request);
        PaymentResponse paymentResponse = response.getBody();

        if (paymentResponse == null) {
            throw new PaymentFailureException("No response from payment service.");
        }

        if (paymentResponse.status() == PaymentStatus.FAILURE) {
            throw new PaymentFailureException("Failure to effect premium payment for userId " + userId);
        }
    }

    private PremiumDto createPremium(Long userId, PremiumPeriod premiumPeriod) {
        LocalDateTime now = LocalDateTime.now();
        PremiumDto premium = new PremiumDto();
        premium.setUserId(userId);
        premium.setStartDate(now);
        premium.setEndDate(now.plusDays(premiumPeriod.getDays()));

        return premium;
    }
}
