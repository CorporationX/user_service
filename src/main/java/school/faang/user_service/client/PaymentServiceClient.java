package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.entity.premium.PaymentRequest;
import school.faang.user_service.entity.premium.PaymentResponse;

@FeignClient(value = "payment-service", url = "${payment-service.host}:9080")
public interface PaymentServiceClient {

    @PostMapping("api/payment")
    ResponseEntity<PaymentResponse> sendPayment(@RequestBody PaymentRequest dto);
}
