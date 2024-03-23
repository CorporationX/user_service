package school.faang.user_service.service.premium;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.payment.Currency;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.validation.premium.PremiumValidator;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PremiumService {

    private final PaymentServiceClient paymentService;
    private final PremiumRepository premiumRepository;
    private final UserRepository userRepository;
    private final PremiumMapper premiumMapper;
    private final PremiumValidator premiumValidator;

    @Transactional
    public PremiumDto buyPremium(Long userId, PremiumPeriod period) {
        premiumValidator.validateUserPremiumStatus(userId);
        PaymentResponse response = paymentService.sendPayment(getPaymentRequest(userId, period));
        premiumValidator.validatePaymentResponse(response);
        return savePremium(userId, period);
    }

    @Transactional
    public void deleteExpiredPremiums() {
        List<Premium> expiredPremiums = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());
        premiumRepository.deleteAll(expiredPremiums);
    }

    private PremiumDto savePremium(Long userId, PremiumPeriod period) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new EntityNotFoundException(String.format("User with ID %d not found", userId)));
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(period.getDays());
        Premium premium = getPremium(user, startDate, endDate);
        return premiumMapper.toDto(premiumRepository.save(premium));
    }

    private Premium getPremium(User user, LocalDateTime startDate, LocalDateTime endDate) {
        return Premium.builder()
                .user(user)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    private PaymentRequest getPaymentRequest(Long userId, PremiumPeriod period) {
        return PaymentRequest.builder()
                .paymentNumber(userId)
                .amount(period.getPrice())
                .currency(Currency.USD)
                .build();
    }
}
