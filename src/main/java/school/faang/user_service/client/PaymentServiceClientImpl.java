package school.faang.user_service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.dto.client.PaymentRequest;
import school.faang.user_service.dto.client.PaymentResponse;
import school.faang.user_service.exception.PaymentFailedException;
import school.faang.user_service.validator.PaymentValidator;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentServiceClientImpl implements PaymentServiceClient {

    private static final String PAYMENT_SERVICE_URL = "http://localhost:9080/api/payment";

    private final RestTemplate restTemplate;
    private final PaymentValidator validator;

    @Override
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        try {
            log.info("Processing payment for user with ID: {}", paymentRequest.userId());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<PaymentRequest> request = new HttpEntity<>(paymentRequest, headers);

            ResponseEntity<PaymentResponse> response = restTemplate.exchange(
                    PAYMENT_SERVICE_URL,
                    HttpMethod.POST,
                    request,
                    PaymentResponse.class
            );

            validator.verifyPayment(response);
            return response.getBody();

        } catch (RestClientException e) {
            String message = "Failed to process payment for user with ID: %d".formatted(paymentRequest.userId());
            log.error(message);
            throw new PaymentFailedException(message);
        }
    }
}
