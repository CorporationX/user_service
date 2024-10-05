package school.faang.user_service.service.payment;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.client.Currency;
import school.faang.user_service.dto.client.PaymentRequest;
import school.faang.user_service.dto.client.PaymentResponse;
import school.faang.user_service.dto.client.PaymentStatus;
import school.faang.user_service.exception.PaymentFailureException;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentServiceClient paymentServiceClient;

    public void sendPayment(Long price, Long paymentNumber) {
        PaymentRequest request = new PaymentRequest(
                paymentNumber,
                BigDecimal.valueOf(price),
                Currency.USD
        );

        ResponseEntity<PaymentResponse> response = paymentServiceClient.sendPayment(request);
        PaymentResponse paymentResponse = response.getBody();

        if (paymentResponse == null) {
            throw new PaymentFailureException("No response from payment service.");
        }

        if (paymentResponse.status() == PaymentStatus.FAILURE) {
            throw new PaymentFailureException("Failure to effect promotion payment for paymentNumber " + paymentNumber);
        }
    }
}
