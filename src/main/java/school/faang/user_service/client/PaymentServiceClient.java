package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.entity.premium.PaymentResponse;
import school.faang.user_service.entity.premium.PaymentRequest;

@FeignClient(name = "payment-service",
        url = "${payment.service.host}:${payment.service.port}")
public interface PaymentServiceClient {
    @PostMapping("/payment")
    ResponseEntity<PaymentResponse> sendPayment(@RequestBody PaymentRequest request);
}
