package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.client.PaymentRequest;
import school.faang.user_service.dto.client.PaymentResponse;
import school.faang.user_service.dto.premiun.PremiumDto;
import school.faang.user_service.dto.premiun.PremiumRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Currency;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.validator.PaymentValidator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {

    private final PremiumRepository premiumRepository;
    private final UserRepository userRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final PaymentValidator paymentValidator;
    private final PremiumMapper mapper;

    @Override
    @Transactional
    public PremiumDto buyPremium(PremiumRequest request, long id) {
        int days = request.days();
        Currency currency = request.currency();
        log.info("Buy premium for user with ID {} and currency {}", id, currency);

        return premiumRepository.findByUserId(id)
                .map(mapper::toDto)
                .orElseGet(() -> {
                    PremiumPeriod period = PremiumPeriod.fromDays(days);
                    BigDecimal amount = period.getPrice(currency);

                    processPayment(id, currency, amount);

                    Premium newPremium = savePremiumForUser(id, period);
                    log.info("Premium for user with ID \"{}\" has been built and saved successfully", id);
                    return mapper.toDto(newPremium);
                });
    }

    private void processPayment(long id, Currency currency, BigDecimal amount) {
        PaymentRequest paymentRequest = new PaymentRequest(id, amount, currency);
        ResponseEntity<PaymentResponse> response = paymentServiceClient.processPayment(paymentRequest);
        paymentValidator.verifyPayment(response);
    }

    private Premium savePremiumForUser(long id, PremiumPeriod period) {
        log.info("Save premium for user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("User not found"));

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(period.getDays());
        Premium premium = Premium.builder()
                .user(user)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        return premiumRepository.save(premium);
    }
}
