package school.faang.user_service.client;

import school.faang.user_service.dto.client.PaymentRequest;
import school.faang.user_service.dto.client.PaymentResponse;


public interface PaymentServiceClient {

    PaymentResponse processPayment(PaymentRequest paymentRequest);
}
