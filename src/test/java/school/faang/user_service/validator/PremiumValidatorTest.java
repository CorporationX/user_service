package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.premium.PremiumRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PremiumValidatorTest {

    @Mock
    private PremiumRepository premiumRepository;

    private PremiumValidator premiumValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        premiumValidator = new PremiumValidator(premiumRepository);
    }

    @Test
    void validateUserId_whenUserDoesNotHavePremium_thenDoNothing() {
        long userId = 1L;
        when(premiumRepository.existsByUserId(userId)).thenReturn(false);

        assertDoesNotThrow(() -> premiumValidator.validateUserId(userId));
    }

    @Test
    void validateUserId_whenUserAlreadyHasPremium_thenThrowException() {
        long userId = 1L;
        when(premiumRepository.existsByUserId(userId)).thenReturn(true);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> premiumValidator.validateUserId(userId));

        assertEquals("Пользователь уже имеет премиум подписку", dataValidationException.getMessage());
    }

    @Test
    void validateResponseStatus_whenPaymentSuccess_thenDoNothing() {
        PaymentResponse paymentResponse = new PaymentResponse(PaymentStatus.SUCCESS, 1, 1, null, null, null);

        assertDoesNotThrow(() -> premiumValidator.validateResponseStatus(paymentResponse));
    }

    @Test
    void validateResponseStatus_whenPaymentFailure_thenThrowException() {
        PaymentResponse paymentResponse = new PaymentResponse(PaymentStatus.FAILURE, 1, 1, null, null, null);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> premiumValidator.validateResponseStatus(paymentResponse));

        assertEquals("Ошибка платежа", dataValidationException.getMessage());

    }
}
