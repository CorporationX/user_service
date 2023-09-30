package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import school.faang.user_service.dto.payment.PaymentResponse;
import school.faang.user_service.dto.payment.PaymentRequest;

@FeignClient(name = "payment-service", url = "${payment-service.host}:${payment-service.port}")
public interface PaymentServiceClient {

    @GetMapping("/api/payment")
    ResponseEntity<PaymentResponse> sendPayment(PaymentRequest request);
}
