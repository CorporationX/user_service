package school.faang.user_service.service.user.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.types.Currency;
import school.faang.user_service.dto.payment.types.PaymentStatus;
import school.faang.user_service.dto.types.PremiumPeriod;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.exception.PaymentException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {

    private final PremiumRepository premiumRepository;
    private final PremiumMapper premiumMapper;
    private final PaymentServiceClient paymentServiceClient;

    @Override
    @Transactional
    public void buyPremium(Long userId, PremiumPeriod premiumPeriod) {

        ResponseEntity<PaymentResponse> paymentResult = paymentServiceClient.sendPayment(new PaymentRequest(1, premiumPeriod.getPrice(), Currency.USD));

        if (!paymentResult.getStatusCode().equals(HttpStatusCode.valueOf(200))
                || !paymentResult.getBody().status().equals(PaymentStatus.SUCCESS)
        ) {
            throw new PaymentException("Payment declined");
        }

        if (!premiumRepository.existsByUserId(userId)) {
            Premium premium = premiumMapper.toEntity(userId, LocalDateTime.now(), LocalDateTime.now().plusDays(premiumPeriod.getDays()));
            premiumRepository.save(premium);
            return;
        }

        Premium premium = premiumRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Premium with userId $s not found", userId)));

        boolean isExpired = premium.getEndDate().isBefore(LocalDateTime.now());
        if (isExpired) {
            premium.setStartDate(LocalDateTime.now());
        }

        premium.setEndDate(premium.getEndDate().plusDays(premiumPeriod.getDays()));
        premiumRepository.save(premium);
    }
}
