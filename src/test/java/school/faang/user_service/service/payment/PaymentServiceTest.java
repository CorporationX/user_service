package school.faang.user_service.service.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.client.Currency;
import school.faang.user_service.dto.client.PaymentRequest;
import school.faang.user_service.dto.client.PaymentResponse;
import school.faang.user_service.dto.client.PaymentStatus;
import school.faang.user_service.exception.PaymentFailureException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    private static final Long PAYMENT_NUMBER = 123456789L;
    private static final Long PRICE = 100L;

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    private PaymentRequest paymentRequest;
    private PaymentResponse successfulPaymentResponse;
    private PaymentResponse failedPaymentResponse;

    @BeforeEach
    void setUp() {
        paymentRequest = new PaymentRequest(
                PAYMENT_NUMBER,
                BigDecimal.valueOf(PRICE),
                Currency.USD
        );
        successfulPaymentResponse = new PaymentResponse(
                PaymentStatus.SUCCESS,
                12345,
                PAYMENT_NUMBER,
                BigDecimal.valueOf(PRICE),
                Currency.USD,
                Currency.EUR,
                "Payment successful"
        );
        failedPaymentResponse = new PaymentResponse(
                PaymentStatus.FAILURE,
                12345,
                PAYMENT_NUMBER,
                BigDecimal.valueOf(PRICE),
                Currency.USD,
                Currency.EUR,
                "Payment failed"
        );
    }

    @Nested
    class SendPaymentTests {

        @Test
        @DisplayName("Successfully send payment")
        void whenSendPaymentThenSuccess() {
            when(paymentServiceClient.sendPayment(any(PaymentRequest.class))).thenReturn(ResponseEntity.ok(successfulPaymentResponse));

            assertDoesNotThrow(() -> paymentService.sendPayment(PRICE, PAYMENT_NUMBER));

            verify(paymentServiceClient).sendPayment(paymentRequest);
        }

        @Test
        @DisplayName("Throws exception when no response from payment service")
        void whenNoResponseThenThrowException() {
            when(paymentServiceClient.sendPayment(any(PaymentRequest.class))).thenReturn(ResponseEntity.of(Optional.empty()));

            PaymentFailureException exception = assertThrows(PaymentFailureException.class, () -> paymentService.sendPayment(PRICE, PAYMENT_NUMBER));

            assertEquals("No response from payment service.", exception.getMessage());
            verify(paymentServiceClient).sendPayment(paymentRequest);
        }

        @Test
        @DisplayName("Throws exception when payment fails")
        void whenPaymentFailureThenThrowException() {
            when(paymentServiceClient.sendPayment(any(PaymentRequest.class))).thenReturn(ResponseEntity.ok(failedPaymentResponse));

            PaymentFailureException exception = assertThrows(PaymentFailureException.class, () -> paymentService.sendPayment(PRICE, PAYMENT_NUMBER));

            assertEquals("Failure to effect promotion payment for paymentNumber " + PAYMENT_NUMBER, exception.getMessage());
            verify(paymentServiceClient).sendPayment(paymentRequest);
        }
    }
}
