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

class ValidatorPremiumTest {

    @Mock
    private PremiumRepository premiumRepository;

    private ValidatorPremium validatorPremium;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validatorPremium = new ValidatorPremium(premiumRepository);
    }

    @Test
    void validateUserId_whenUserDoesNotHavePremium_thenDoNothing() {
        long userId = 1L;
        when(premiumRepository.existsByUserId(userId)).thenReturn(false);

        assertDoesNotThrow(() -> validatorPremium.validateUserId(userId));
    }

    @Test
    void validateUserId_whenUserAlreadyHasPremium_thenThrowException() {
        long userId = 1L;
        when(premiumRepository.existsByUserId(userId)).thenReturn(true);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> validatorPremium.validateUserId(userId));

        assertEquals("Пользователь уже имеет премиум подписку", dataValidationException.getMessage());
    }

    @Test
    void validateResponseStatus_whenPaymentSuccess_thenDoNothing() {
        PaymentResponse paymentResponse = new PaymentResponse(PaymentStatus.SUCCESS, 1, 1, null, null, null);

        assertDoesNotThrow(() -> validatorPremium.validateResponseStatus(paymentResponse));
    }

    @Test
    void validateResponseStatus_whenPaymentFailure_thenThrowException() {
        PaymentResponse paymentResponse = new PaymentResponse(PaymentStatus.FAILURE, 1, 1, null, null, null);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> validatorPremium.validateResponseStatus(paymentResponse));

        assertEquals("Ошибка платежа", dataValidationException.getMessage());

    }
}
