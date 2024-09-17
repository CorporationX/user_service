package school.faang.user_service.service.premium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.payment.PaymentStatus;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.payment.UnSuccessPaymentException;
import school.faang.user_service.exception.premium.PremiumValidationFailureException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static school.faang.user_service.service.premium.util.PremiumErrorMessages.UNSUCCESSFUL_PREMIUM_PAYMENT;
import static school.faang.user_service.service.premium.util.PremiumErrorMessages.USER_ALREADY_HAS_PREMIUM;
import static school.faang.user_service.util.premium.PremiumFabric.getPaymentResponse;
import static school.faang.user_service.util.premium.PremiumFabric.getPremium;
import static school.faang.user_service.util.premium.PremiumFabric.getUser;

@ExtendWith(MockitoExtension.class)
class PremiumValidationServiceTest {
    private static final long USER_ID = 1L;
    private static final long PREMIUM_ID = 1L;
    private static final PremiumPeriod PERIOD = PremiumPeriod.MONTH;
    private static final LocalDateTime START_DATE = LocalDateTime.now();
    private static final LocalDateTime END_DATE = START_DATE.plusDays(PERIOD.getDays());
    private static final String MESSAGE = "test message";

    @InjectMocks
    private PremiumValidationService premiumValidationService;

    @Test
    @DisplayName("Given user with premium when check then throw exception")
    void testCheckUserForSubPeriodActivePremium() {
        Premium premium = getPremium(PREMIUM_ID, START_DATE, END_DATE);
        User user = getUser(USER_ID, premium);

        assertThatThrownBy(() -> premiumValidationService.validateUserForSubPeriod(USER_ID, user.getPremium()))
                .isInstanceOf(PremiumValidationFailureException.class)
                .hasMessageContaining(USER_ALREADY_HAS_PREMIUM, USER_ID, END_DATE);
    }

    @Test
    @DisplayName("Given not success response when check then throw exception")
    void testCheckPaymentResponseUnSuccessPaymentResponse() {
        PaymentResponseDto paymentResponse = getPaymentResponse(PaymentStatus.FAILED, MESSAGE);

        assertThatThrownBy(() ->
                premiumValidationService.checkPaymentResponse(paymentResponse, USER_ID, PremiumPeriod.MONTH))
                .isInstanceOf(UnSuccessPaymentException.class)
                .hasMessageContaining(UNSUCCESSFUL_PREMIUM_PAYMENT, USER_ID, PERIOD.getDays(), paymentResponse.message());
    }
}