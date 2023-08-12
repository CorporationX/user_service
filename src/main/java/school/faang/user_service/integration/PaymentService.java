package school.faang.user_service.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import school.faang.user_service.client.PaymentFeignClient;
import school.faang.user_service.dto.premium.PremiumResponseDto;
import school.faang.user_service.mapper.premium.PremiumResponseMapper;
import school.faang.user_service.model.Payment;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PremiumResponseMapper premiumResponseMapper;
    private final PaymentFeignClient paymentFeignClient;

    @Retryable(value = {Exception.class},
        maxAttemptsExpression = "#{${payment-service.retry.max-attempts}}",
        backoff = @Backoff(delayExpression = "#{${payment-service.retry.delay-ms}}"))
    public PremiumResponseDto makePayment(Payment payment) {
        try {
            ResponseEntity<PaymentResponse> exchange = paymentFeignClient.makePayment(payment);
            if (exchange.getStatusCode() == HttpStatus.OK) {
                return premiumResponseMapper.toDto(exchange.getBody());
            } else {
                throw new IllegalArgumentException("Payment failed");
            }
        } catch (ResourceAccessException e) {
            throw new IllegalStateException("Failed to connect to the payment service: " + e.getMessage());
        }
    }
}
