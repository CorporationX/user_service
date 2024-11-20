package school.faang.user_service.client.payment;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.premium.PaymentRequestDto;

@FeignClient(name = "payment-service", url="http://localhost:9080")
public interface PaymentServiceClient {
    @PostMapping("/payment")
    boolean sendPaymentRequest(@RequestBody PaymentRequestDto paymentRequest);
}
