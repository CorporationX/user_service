package school.faang.user_service.service.payment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.payment.PaymentServiceClient;
import school.faang.user_service.dto.payment.PaymentRequestDto;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.entity.promotion.PromotionTariff;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    private static final PremiumPeriod PREMIUM_PERIOD = PremiumPeriod.MONTH;
    private static final PromotionTariff PROMOTION_TARIFF = PromotionTariff.STANDARD;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void testSendPaymentPremiumPeriodSuccessful() {
        paymentService.sendPayment(PREMIUM_PERIOD);
        verify(paymentServiceClient).sendPayment(any(PaymentRequestDto.class));
    }

    @Test
    void testSendPaymentPromotionTariffSuccessful() {
        paymentService.sendPayment(PROMOTION_TARIFF);
        verify(paymentServiceClient).sendPayment(any(PaymentRequestDto.class));
    }
}