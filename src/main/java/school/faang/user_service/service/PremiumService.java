package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.premium.Currency;
import school.faang.user_service.dto.premium.PaymentRequest;
import school.faang.user_service.dto.premium.PaymentResponse;
import school.faang.user_service.dto.premium.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.PaymentFailedException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PremiumService {

    private final PremiumRepository premiumRepository;
    private final UserRepository userRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final PremiumMapper premiumMapper;

    public PremiumDto buyPremium(long userId, PremiumPeriod premiumPeriod) {
        if (premiumRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("User with %d already has a premium", userId));
        }

        long paymentNumber = premiumRepository.getLastId() + 1;
        PaymentRequest paymentRequest =
                new PaymentRequest(paymentNumber, new BigDecimal(premiumPeriod.getPrice()), Currency.USD);
        ResponseEntity<PaymentResponse> paymentResponseEntity = paymentServiceClient.sendPayment(paymentRequest);
        if (paymentResponseEntity.getBody() != null
                && paymentResponseEntity.getBody().status().equals(PaymentStatus.SUCCESS)) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("User with %d not exists", userId)));
            Premium premium = new Premium();
            premium.setUser(user);
            LocalDateTime startDate = LocalDateTime.now();
            LocalDateTime endDate = startDate.plusDays(premiumPeriod.getDays());
            premium.setStartDate(startDate);
            premium.setEndDate(endDate);
            Premium savePremium = premiumRepository.save(premium);
            return premiumMapper.toDto(savePremium);
        } else {
            throw new PaymentFailedException("Payment failed try again");
        }
    }
}

