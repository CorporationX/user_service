package school.faang.user_service.client;

import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.client.PaymentRequest;
import school.faang.user_service.dto.client.PaymentResponse;


public interface PaymentServiceClient {

    ResponseEntity<PaymentResponse> processPayment(PaymentRequest paymentRequest);
}
