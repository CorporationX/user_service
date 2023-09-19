package school.faang.user_service.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.premium.Currency;
import school.faang.user_service.dto.premium.PaymentRequest;
import school.faang.user_service.dto.premium.PaymentResponse;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.validator.PremiumValidator;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class PremiumService {

    private final PremiumRepository premiumRepository;
    private final UserRepository userRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final PremiumValidator premiumValidator;
    private final PremiumMapper premiumMapper;
    private final Clock clock = Clock.systemUTC();
    private final UserRatingService userRatingService;

    @Transactional
    public PremiumDto buyPremium(long userId, PremiumPeriod premiumPeriod) {
        premiumValidator.validateExistPremiumFromUser(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with %d not exists", userId)));

        paymentPremium(premiumPeriod);

        Premium savePremium = savePremiumToRepository(premiumPeriod, user);
        userRatingService.addRatingForPremium(userId);
        return premiumMapper.toDto(savePremium);
    }

    @Transactional
    public void delete(Premium premium) {
        premiumRepository.delete(premium);
    }

    public List<Premium> findAllPremium() {
        return StreamSupport.stream(premiumRepository.findAll().spliterator(), false)
                .toList();
    }

    @Retryable(retryFor = FeignException.class)
    private void paymentPremium(PremiumPeriod premiumPeriod) {
        long payNumber = premiumRepository.count();
        PaymentRequest paymentRequest =
                new PaymentRequest(payNumber, new BigDecimal(premiumPeriod.getPrice()), Currency.USD);
        ResponseEntity<PaymentResponse> paymentResponseEntity = paymentServiceClient.sendPayment(paymentRequest);
        PaymentResponse response = paymentResponseEntity.getBody();
        premiumValidator.validateResponse(response);
    }

    private Premium savePremiumToRepository(PremiumPeriod premiumPeriod, User user) {
        Premium premium = new Premium();
        premium.setUser(user);
        LocalDateTime startDate = LocalDateTime.now(clock);
        premium.setStartDate(startDate);
        LocalDateTime endDate = startDate.plusDays(premiumPeriod.getDays());
        premium.setEndDate(endDate);
        return premiumRepository.save(premium);
    }
}