package school.faang.user_service.service.premium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.payment.PaymentServiceClient;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.payment.PaymentStatus;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.payment.UnSuccessPaymentException;
import school.faang.user_service.exception.premium.PremiumCheckFailureException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.service.premium.util.PremiumErrorMessages.UNSUCCESSFUL_PAYMENT;
import static school.faang.user_service.service.premium.util.PremiumErrorMessages.USER_ALREADY_HAS_PREMIUM;
import static school.faang.user_service.util.PremiumFabric.getPaymentResponse;
import static school.faang.user_service.util.PremiumFabric.getPremium;
import static school.faang.user_service.util.PremiumFabric.getUser;

@ExtendWith(MockitoExtension.class)
class PremiumServiceTest {
    private static final long USER_ID = 1L;
    private static final long PREMIUM_ID = 1L;
    private static final PremiumPeriod PERIOD = PremiumPeriod.MONTH;
    private static final LocalDateTime START_DATE = LocalDateTime.of(2000, 1, 1, 1, 1);
    private static final LocalDateTime END_DATE = START_DATE.plusDays(PERIOD.getDays());

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @InjectMocks
    private PremiumService premiumService;

    @Test
    @DisplayName("Given user with premium when check then throw exception")
    void testBuyPremiumCheckUserForSubPeriod() {
        Premium premium = getPremium(PREMIUM_ID, START_DATE, END_DATE);
        User user = getUser(USER_ID, premium);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> premiumService.buyPremium(USER_ID, PremiumPeriod.MONTH))
                .isInstanceOf(PremiumCheckFailureException.class)
                .hasMessageContaining(USER_ALREADY_HAS_PREMIUM, END_DATE);
    }

    @Test
    @DisplayName("Given not success response when check then throw exception")
    void testBuyPremiumNotSuccessResponse() {
        User user = getUser(USER_ID, null);
        PaymentResponse notSuccessResponse = getPaymentResponse(PaymentStatus.NOT_SUCCESS);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(paymentServiceClient.sendPayment(any(PaymentRequest.class))).thenReturn(notSuccessResponse);

        assertThatThrownBy(() -> premiumService.buyPremium(USER_ID, PremiumPeriod.MONTH))
                .isInstanceOf(UnSuccessPaymentException.class)
                .hasMessageContaining(UNSUCCESSFUL_PAYMENT, USER_ID, PERIOD.getDays());
    }

    @Test
    @DisplayName("Buy premium successful")
    void testBuyPremiumSuccessful() {
        User user = getUser(USER_ID, null);
        PaymentResponse successResponse = getPaymentResponse(PaymentStatus.SUCCESS);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(paymentServiceClient.sendPayment(any(PaymentRequest.class))).thenReturn(successResponse);
        premiumService.buyPremium(USER_ID, PERIOD);

        verify(premiumRepository).save(any(Premium.class));
    }
}