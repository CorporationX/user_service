package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.integration.PaymentResponse;
import school.faang.user_service.model.Payment;

@FeignClient(name = "payment-service", url = "${payment-service.host}:${payment-service.port}")
public interface PaymentFeignClient {

    @PostMapping("${payment-service.endpoint}")
    ResponseEntity<PaymentResponse> makePayment(@RequestBody Payment payment);
}
