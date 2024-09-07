package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.dto.client.PaymentResponse;
import school.faang.user_service.dto.client.PaymentStatus;
import school.faang.user_service.entity.premium.Currency;
import school.faang.user_service.exception.PaymentFailedException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class PaymentValidatorTest {

    private final PaymentValidator validator = new PaymentValidator();

    @Test
    void verifyPayment_ResponseBodyIsNotNull() {
        var responseEntity = createAndConfigureSpyResponseEntity(
                PaymentStatus.SUCCESS,
                HttpStatusCode.valueOf(200),
                123
        );
        assertDoesNotThrow(() -> validator.verifyPayment(responseEntity));
    }

    @Test
    void verifyPayment_ResponseBodyIsNull() {
        String message = "Response is null";
        var responseEntity = createAndConfigureSpyResponseEntity(
                PaymentStatus.SUCCESS,
                HttpStatusCode.valueOf(200),
                123
        );
        when(responseEntity.getBody()).thenReturn(null);

        var exception = assertThrows(PaymentFailedException.class,
                () -> validator.verifyPayment(responseEntity));

        assertEquals(message, exception.getMessage());
    }

    @Test
    void verifyPayment_IsSuccessPayment_EverythingRight() {
        var responseEntity = createAndConfigureSpyResponseEntity(
                PaymentStatus.SUCCESS,
                HttpStatusCode.valueOf(200),
                123
        );

        assertDoesNotThrow(() -> validator.verifyPayment(responseEntity));
        verify(responseEntity).getBody();
        verify(responseEntity).getStatusCode();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "null, 200, 123",
            "SUCCESS, 404, 123",
            "SUCCESS, 200, 0"
    }, nullValues = "null")
    void verifyPayment_IsSuccessPayment(PaymentStatus status, int code, int verificationCode) {
        String message = "Failed to process payment request: %s".formatted(verificationCode);
        var responseEntity = createAndConfigureSpyResponseEntity(
                status,
                HttpStatusCode.valueOf(code),
                verificationCode
        );

        var exception = assertThrows(PaymentFailedException.class,
                () -> validator.verifyPayment(responseEntity));
        assertEquals(message, exception.getMessage());
    }

    private ResponseEntity<PaymentResponse> createAndConfigureSpyResponseEntity(
            PaymentStatus paymentStatus, HttpStatusCode code, int verificationCode) {
        var response = new PaymentResponse(
                paymentStatus,
                verificationCode,
                1L,
                new BigDecimal(80),
                Currency.USD,
                "Hello world!"
        );
        ResponseEntity<PaymentResponse> responseEntity = new ResponseEntity<>(response, code);

        var spyResponseEntity = Mockito.spy(responseEntity);
        when(spyResponseEntity.getBody()).thenReturn(response);
        when(spyResponseEntity.getStatusCode()).thenReturn(code);

        return spyResponseEntity;
    }
}