package school.faang.user_service.client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.model.dto.PaymentRequest;
import school.faang.user_service.model.dto.PaymentResponse;

@FeignClient(value = "payment-service", url = "${payment-service.host}:${payment-service.port}")
public interface PaymentServiceClient {

    @PostMapping("/api/v1/payment")
    @Retryable(
            value = {FeignException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000)
    )
    ResponseEntity<PaymentResponse> sendPayment(@RequestBody PaymentRequest request);
}
