package school.faang.user_service.integration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.dto.premium.PremiumResponseDto;
import school.faang.user_service.mapper.premium.PremiumResponseMapper;
import school.faang.user_service.model.Payment;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class PaymentService { //todo test

    @Getter
    private static final int MAX_RETRIES = 5;
    @Getter
    private static final int RETRY_DELAY_MS = 3000;

    private final UrlBuilder urlBuilder;
    private final RestTemplate restTemplate;
    private final PremiumResponseMapper premiumResponseMapper;

    @Value("${payment-service.host}")
    private String paymentServiceHost;

    @Value("${payment-service.port}")
    private String paymentServicePort;


    @Retryable(value = {ResourceAccessException.class}, maxAttempts = MAX_RETRIES, backoff = @Backoff(delay = RETRY_DELAY_MS))
    public PremiumResponseDto makePayment(Payment payment) {
        URI uri = urlBuilder.buildUrl(paymentServiceHost, paymentServicePort, ApiEndpoints.PAYMENT);

        try {
            ResponseEntity<PaymentResponse> exchange = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(payment),
                PaymentResponse.class
            );
            return premiumResponseMapper.toDto(exchange.getBody());
        } catch (ResourceAccessException e) {
            throw new IllegalStateException("Failed to connect to the payment service: " + e.getMessage());
        }
    }
}
