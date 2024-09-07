package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.premiun.PremiumDto;
import school.faang.user_service.entity.premium.Currency;
import school.faang.user_service.dto.client.PaymentRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.validator.PremiumValidator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {

    private final PremiumRepository premiumRepository;
    private final UserRepository userRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final PremiumMapper mapper;
    private final PremiumValidator validator;

    @Override
    @Transactional
    public PremiumDto buyPremium(long id, Integer days, Currency currency) {
        log.info("Buy premium for user with ID \"{}\" and currency \"{}\"", id, currency);

        boolean existPremium = premiumRepository.existsByUserId(id);
        validator.validate(id, existPremium);

        PremiumPeriod period = PremiumPeriod.fromDays(days);
        BigDecimal amount = period.getPrice(currency);
        PaymentRequest paymentRequest = new PaymentRequest(id, amount, currency);
        paymentServiceClient.processPayment(paymentRequest);

        Premium premium = savePremiumForUser(id, period);
        log.info("Premium with ID \"{}\" has been built and saved successfully", id);
        return mapper.toDto(premium);
    }

    private Premium savePremiumForUser(long id, PremiumPeriod period) {
        log.info("Save premium for user with ID: {}", id);
        User user = userRepository.findById(id).get();
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
