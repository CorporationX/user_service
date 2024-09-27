package school.faang.user_service.service.payment;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.payment.PaymentServiceClient;
import school.faang.user_service.dto.payment.PaymentRequestDto;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.entity.promotion.PromotionTariff;

import java.math.BigDecimal;

@Slf4j
@EnableRetry
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentServiceClient paymentServiceClient;

    @Retryable(
            retryFor = FeignException.class,
            maxAttemptsExpression = "${app.payment_service.retryable.send_payment.max_attempts}",
            backoff = @Backoff(
                    delayExpression = "${app.payment_service.retryable.send_payment.delay}",
                    multiplierExpression = "${app.payment_service.retryable.send_payment.multiplier}"
            )
    )
    public PaymentResponseDto sendPayment(PremiumPeriod period) {
        log.info("Send payment for premium period with: {}", period);
        var paymentRequest = PaymentRequestDto
                .builder()
                .paymentNumber(System.currentTimeMillis())
                .amount(BigDecimal.valueOf(period.getCost()))
                .currency(period.getCurrency())
                .build();
        return paymentServiceClient.sendPayment(paymentRequest);
    }

    @Retryable(
            retryFor = FeignException.class,
            maxAttemptsExpression = "${app.payment_service.retryable.send_payment.max_attempts}",
            backoff = @Backoff(
                    delayExpression = "${app.payment_service.retryable.send_payment.delay}",
                    multiplierExpression = "${app.payment_service.retryable.send_payment.multiplier}"
            )
    )
    public PaymentResponseDto sendPayment(PromotionTariff tariff) {
        log.info("Send payment for promotion tariff: {}", tariff);
        var paymentRequest = PaymentRequestDto
                .builder()
                .paymentNumber(System.currentTimeMillis())
                .amount(BigDecimal.valueOf(tariff.getCost()))
                .currency(tariff.getCurrency())
                .build();
        return paymentServiceClient.sendPayment(paymentRequest);
    }
}
