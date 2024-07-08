package school.faang.user_service.client.paymentService;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.client.paymentService.model.PaymentRequest;
import school.faang.user_service.client.paymentService.model.PaymentResponse;

/**
 * @author Evgenii Malkov
 */
@Component
@FeignClient(
        name = "paymentServiceClient",
        url = "${payment-service.host}:${payment-service.port}")
public interface PaymentServiceClient {

    @PostMapping("${payment-service.api}${payment-service.sendPayment}")
    PaymentResponse sendPayment(@RequestBody PaymentRequest request);
}
