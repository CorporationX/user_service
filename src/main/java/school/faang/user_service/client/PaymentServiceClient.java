package school.faang.user_service.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.dto.Currency;
import school.faang.user_service.dto.PaymentRequest;
import school.faang.user_service.dto.PaymentResponse;

@Component
@RequiredArgsConstructor
public class PaymentServiceClient {
    private final RestTemplate restTemplate;

    @Value("${payment-service.host}")
    private String host;

    @Value("${payment-service.port}")
    private int port;

    public PaymentResponse sendPaymentRequest(double amount, Currency currency) {
        PaymentRequest paymentRequest = PaymentRequest.builder()
            .amount(amount)
            .currency(currency)
            .build();
        String url = String.format("http://%s:%d", host, port);
        String endpoint = "/api/payment";
        return restTemplate.exchange(
            url + endpoint,
            HttpMethod.POST,
            new HttpEntity<>(paymentRequest),
            PaymentResponse.class
        ).getBody();
    }
}
