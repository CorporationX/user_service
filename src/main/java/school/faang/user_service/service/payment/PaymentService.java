package school.faang.user_service.service.payment;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.client.Currency;
import school.faang.user_service.dto.client.PaymentRequest;
import school.faang.user_service.dto.client.PaymentResponse;
import school.faang.user_service.dto.client.PaymentStatus;
import school.faang.user_service.exception.PaymentFailureException;

import java.math.BigDecimal;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentServiceClient paymentServiceClient;

    public void sendPayment(Long price, Long paymentNumber) {
        log.info("Sending payment request. Payment number: {}, Amount: {}", paymentNumber, price);

        PaymentRequest request = new PaymentRequest(
                paymentNumber,
                BigDecimal.valueOf(price),
                Currency.USD
        );

        ResponseEntity<PaymentResponse> response = paymentServiceClient.sendPayment(request);
        PaymentResponse paymentResponse = response.getBody();

        if (paymentResponse == null) {
            log.error("No response from payment service for payment number: {}", paymentNumber);
            throw new PaymentFailureException("No response from payment service.");
        }

        if (paymentResponse.status() == PaymentStatus.FAILURE) {
            log.error("Payment failed for payment number: {}", paymentNumber);
            throw new PaymentFailureException("Failure to effect promotion payment for paymentNumber " + paymentNumber);
        }

        log.info("Payment successfully processed for payment number: {}", paymentNumber);
    }
}
