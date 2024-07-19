package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.PaymentRequest;
import school.faang.user_service.dto.PaymentResponse;

@FeignClient(name = "payment-service", url = "${payment-service.url}")
public interface PaymentServiceClient {
    @PostMapping
    PaymentResponse sendPaymentRequest(@RequestBody PaymentRequest paymentRequest);
}
