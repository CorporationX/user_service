package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {

    @PostMapping("payment")
    ResponseEntity<PaymentResponse> pay(PaymentRequest paymentRequest);
}