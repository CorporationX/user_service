package school.faang.user_service.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.dto.client.PaymentRequest;
import school.faang.user_service.dto.client.PaymentResponse;
import school.faang.user_service.dto.client.PaymentStatus;
import school.faang.user_service.entity.premium.Currency;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.PaymentFailedException;
import school.faang.user_service.validator.PaymentValidator;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceClientImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PaymentServiceClientImpl paymentServiceClient;

    private PaymentRequest paymentRequest;
    private PaymentResponse paymentResponse;

    @BeforeEach
    void setUp() {
        long userId = 1L;
        PremiumPeriod premiumPeriod = PremiumPeriod.MONTH;
        Currency currency = Currency.USD;
        BigDecimal price = premiumPeriod.getPrice(currency);

        paymentRequest = PaymentRequest.builder()
                .userId(userId)
                .currency(currency)
                .amount(price)
                .build();
        paymentResponse = PaymentResponse.builder()
                .userId(userId)
                .currency(currency)
                .message("Hello world!")
                .amount(price)
                .status(PaymentStatus.SUCCESS)
                .verificationCode(123)
                .build();
        paymentServiceClient.setPAYMENT_SERVICE_URL("http://localhost:9080/api/payment");
    }

    @Test
    void processPayment_Success() {
        ResponseEntity<PaymentResponse> responseEntity = ResponseEntity.ok().body(paymentResponse);
        when(restTemplate.exchange(
                eq("http://localhost:9080/api/payment"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(PaymentResponse.class))
        ).thenReturn(responseEntity);

        ResponseEntity<PaymentResponse> response = paymentServiceClient.processPayment(paymentRequest);

        assertEquals(paymentResponse, response.getBody());
    }

    @Test
    void processPayment_Failure() {
        String message = "Failed to process payment for user with ID: %d".formatted(paymentRequest.userId());
        when(restTemplate.exchange(
                eq("http://localhost:9080/api/payment"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(PaymentResponse.class)
        )).thenThrow(new PaymentFailedException(message));

        var exception = assertThrows(PaymentFailedException.class,
                () -> paymentServiceClient.processPayment(paymentRequest));

        assertEquals(message, exception.getMessage());
    }
}
