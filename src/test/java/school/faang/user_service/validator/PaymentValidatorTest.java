package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.PaymentResponse;
import school.faang.user_service.dto.PaymentStatus;
import school.faang.user_service.exception.PaymentFailureException;

import static org.junit.jupiter.api.Assertions.*;

class PaymentValidatorTest {
    private PaymentValidator paymentValidator;

    private PaymentResponse successPaymentResponse;
    private PaymentResponse errorPaymentResponse;

    @BeforeEach
    void setUp() {
        paymentValidator = new PaymentValidator();
        successPaymentResponse = PaymentResponse.builder().status(PaymentStatus.SUCCESS).build();
        errorPaymentResponse = PaymentResponse.builder().status(PaymentStatus.ERROR).build();
    }

    @Test
    void testValidatePaymentSuccess() {
        assertDoesNotThrow(() -> paymentValidator.validatePaymentSuccess(successPaymentResponse));
    }

    @Test
    void testValidatePaymentSuccessThrowsPaymentFailureException() {
        assertThrows(PaymentFailureException.class, () -> paymentValidator.validatePaymentSuccess(errorPaymentResponse));
    }
}
