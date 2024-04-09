package school.faang.user_service.service.premium;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import school.faang.user_service.event.UserPremiumBoughtEvent;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.publisher.PremiumBoughtEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.validation.premium.PremiumValidator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumService {

    private final PaymentServiceClient paymentService;
    private final PremiumRepository premiumRepository;
    private final UserRepository userRepository;
    private final PremiumMapper premiumMapper;
    private final PremiumValidator premiumValidator;
    private final ExecutorService executorService;
    private final PremiumBoughtEventPublisher premiumBoughtEventPublisher;

    @Value("${premium.expired.delete-batch}")
    private Integer batch;

    @Transactional
    public PremiumDto buyPremium(Long userId, PremiumPeriod period) {
        premiumValidator.validateUserPremiumStatus(userId);
        PaymentResponse response = paymentService.sendPayment(getPaymentRequest(userId, period));
        premiumValidator.validatePaymentResponse(response);
        publishPremiumBoughtEvent(userId, response.getAmount(), period);
        return savePremium(userId, period);
    }

    @Transactional
    public void deleteExpiredPremiums() {
        List<Premium> expiredPremiums = premiumRepository.findAllByEndDateBefore(LocalDateTime.now());
        for (int i = 0; i < expiredPremiums.size(); i += batch) {
            final int innerI = i + 1;
            executorService.execute(() -> premiumRepository.deleteAll(expiredPremiums.subList(innerI, Math.min(innerI + batch + 1, expiredPremiums.size()))));
        }
    }

    private void publishPremiumBoughtEvent(long userId, BigDecimal amount, PremiumPeriod period) {
        premiumBoughtEventPublisher.publish(UserPremiumBoughtEvent.builder()
                .userId(userId)
                .amount(amount)
                .premiumPeriod(period.getDays())
                .timestamp(LocalDateTime.now())
                .build());
    }

    private PremiumDto savePremium(Long userId, PremiumPeriod period) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new EntityNotFoundException(String.format("User with ID %d not found", userId)));
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(period.getDays());
        Premium premium = getPremium(user, startDate, endDate);
        Premium savedPremium = premiumRepository.save(premium);
        log.info("Premium saved: {}", premium.getId());
        return premiumMapper.toDto(savedPremium);
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
