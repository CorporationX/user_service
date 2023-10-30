package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.premium.Currency;
import school.faang.user_service.dto.premium.PaymentResponse;
import school.faang.user_service.dto.premium.PaymentStatus;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.PaymentFailedException;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PremiumValidatorTest {

    @InjectMocks
    private PremiumValidator premiumValidator;

    @Mock
    private PremiumRepository premiumRepository;

    @Test
    void testValidateResponseThrowPaymentFailedExceptionWhenStatusIsNull() {
        PaymentResponse response = new PaymentResponse(
                null, 1, 1, new BigDecimal(1), Currency.USD, "Payment failed");
        assertThrows(PaymentFailedException.class, () -> premiumValidator.validateResponse(response));
    }

    @Test
    void testValidateResponseThrowPaymentFailedExceptionWhenResponseIsNull() {
        assertThrows(PaymentFailedException.class, () -> premiumValidator.validateResponse(null));
    }

    @Test
    void testValidateResponseDoesNotThrowPaymentFailedException() {
        PaymentResponse response = new PaymentResponse(
                PaymentStatus.SUCCESS, 1, 1, new BigDecimal(1), Currency.USD, "Payment failed");
        assertDoesNotThrow(() -> premiumValidator.validateResponse(response));
    }

    @Test
    void testValidateExistPremiumFromUserDoesNotThrowEntityNotFoundException() {
        assertDoesNotThrow(() -> premiumValidator.validateExistPremiumFromUser(1));
    }

    @Test
    void testValidateExistPremiumFromUserThrowEntityNotFoundException() {
        Mockito.when(premiumRepository.existsById(1L)).thenReturn(true);
        assertThrows(EntityNotFoundException.class, () -> premiumValidator.validateExistPremiumFromUser(1));
    }
}