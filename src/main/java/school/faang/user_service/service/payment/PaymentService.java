package school.faang.user_service.service.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.payment.PaymentServiceClient;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.entity.promotion.PromotionTariff;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentServiceClient paymentServiceClient;

    public PaymentResponse sendPayment(PremiumPeriod period) {
        var paymentRequest = PaymentRequest
                .builder()
                .paymentNumber(System.currentTimeMillis())
                .amount(BigDecimal.valueOf(period.getCost()))
                .currency(period.getCurrency())
                .build();
        return paymentServiceClient.sendPayment(paymentRequest);
    }

    public PaymentResponse sendPayment(PromotionTariff tariff) {
        var paymentRequest = PaymentRequest
                .builder()
                .paymentNumber(System.currentTimeMillis())
                .amount(BigDecimal.valueOf(tariff.getCost()))
                .currency(tariff.getCurrency())
                .build();
        return paymentServiceClient.sendPayment(paymentRequest);
    }
}
