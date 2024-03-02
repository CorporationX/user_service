package school.faang.user_service.integration.payment;


import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.entity.premium.*;
import school.faang.user_service.exception.PaymentProcessingException;

import java.math.BigDecimal;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentServiceClient paymentServiceClient;

    @Retryable(
            retryFor = FeignException.class,
            maxAttemptsExpression = "${payment-service.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${payment-service.retry.delay-ms}"))
    public void makePayment(PremiumPeriod premiumPeriod) {
        long paymentNumber = new Random().nextInt(1, 1000000000);
        PaymentRequest paymentRequest = new PaymentRequest(paymentNumber, BigDecimal.valueOf(premiumPeriod.getPrice()), Currency.USD);
        ResponseEntity<PaymentResponse> paymentResponseResponseEntity = paymentServiceClient.sendPayment(paymentRequest);
        PaymentResponse paymentResponse = paymentResponseResponseEntity.getBody();
        validatePaymentResponse(paymentResponse);
    }

    @Recover
    private void recover(FeignException ex) {
        throw new PaymentProcessingException(ex.getMessage());
    }

    private void validatePaymentResponse(PaymentResponse paymentResponse) {
        if (paymentResponse == null || paymentResponse.status() == null || !paymentResponse.status().equals(PaymentStatus.SUCCESS)) {
            throw new PaymentProcessingException("The payment failed, please repeat");
        }
    }
}
