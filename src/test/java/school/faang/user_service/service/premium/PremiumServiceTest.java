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
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.util.premium.PremiumFabric.getPaymentResponse;
import static school.faang.user_service.util.premium.PremiumFabric.getPremium;
import static school.faang.user_service.util.premium.PremiumFabric.getUser;

@ExtendWith(MockitoExtension.class)
class PremiumServiceTest {
    private static final long USER_ID = 1L;
    private static final long PREMIUM_ID = 1L;
    private static final PremiumPeriod PERIOD = PremiumPeriod.MONTH;
    private static final LocalDateTime START_DATE = LocalDateTime.now();
    private static final LocalDateTime EXPIRED_DATE = START_DATE.minusDays(1);
    private static final String MESSAGE = "test message";

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private PremiumCheckService premiumCheckService;

    @InjectMocks
    private PremiumService premiumService;

    @Test
    @DisplayName("Given user with expired premium when buy then delete expired premium")
    void testBuyPremiumDeleteExpiredPremium() {
        Premium premium = getPremium(PREMIUM_ID, START_DATE, EXPIRED_DATE);
        User user = getUser(USER_ID, premium);
        PaymentResponse successResponse = getPaymentResponse(PaymentStatus.SUCCESS, MESSAGE);
        when(premiumCheckService.checkUserForSubPeriod(USER_ID)).thenReturn(user);
        when(paymentServiceClient.sendPayment(any(PaymentRequest.class))).thenReturn(successResponse);
        premiumService.buyPremium(USER_ID, PremiumPeriod.MONTH);

        verify(premiumRepository).delete(premium);
    }

    @Test
    @DisplayName("Buy premium successful")
    void testBuyPremiumSuccessful() {
        User user = getUser(USER_ID, null);
        PaymentResponse successResponse = getPaymentResponse(PaymentStatus.SUCCESS, MESSAGE);
        when(premiumCheckService.checkUserForSubPeriod(USER_ID)).thenReturn(user);
        when(paymentServiceClient.sendPayment(any(PaymentRequest.class))).thenReturn(successResponse);
        premiumService.buyPremium(USER_ID, PERIOD);

        verify(premiumRepository).save(any(Premium.class));
    }
}