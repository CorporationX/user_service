package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
class PremiumValidatorTest {

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PremiumValidator premiumValidator;


    @Test
    void validateUserDoesNotHavePremium_whenUserDoesNotHavePremium_thenDoNothing() {
        long userId = 1L;

        assertDoesNotThrow(() -> premiumValidator.validateUserDoesNotHavePremium(userId));
    }

    @Test
    void validateUserDoesNotHavePremium_whenUserAlreadyHasPremium_thenThrowException() {
        long userId = 1L;
        Mockito.doThrow(new DataValidationException("Пользователь уже имеет премиум подписку")).when(userService).validateUserDoesNotHavePremium(userId);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> premiumValidator.validateUserDoesNotHavePremium(userId));

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
