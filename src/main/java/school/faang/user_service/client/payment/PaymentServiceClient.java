package school.faang.user_service.client.payment;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.client.FeignConfig;
import school.faang.user_service.client.dto.PaymentRequest;
import school.faang.user_service.client.dto.PaymentResponse;

@FeignClient(name = "payment-service", url = "${payment-service.url}", configuration = FeignConfig.class)
public interface PaymentServiceClient {

    @PostMapping("/api/payment")
    PaymentResponse processPayment(@RequestBody PaymentRequest request);

    @GetMapping("/api/payments/{id}")
    PaymentResponse getPayment(@PathVariable Long id);
}
