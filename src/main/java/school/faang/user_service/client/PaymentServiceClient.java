package school.faang.user_service.client;


import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.client.PaymentRequest;
import school.faang.user_service.dto.client.PaymentResponse;

@FeignClient(value = "payment-service", url = "${payment-service.host}:${payment-service.port}")
public interface PaymentServiceClient {

    @PostMapping("/api/payment")
    @Retryable(
            value = {FeignException.class},
            maxAttemptsExpression = "${payment-service.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${payment-service.retry.backoff-delay}")
    )
    ResponseEntity<PaymentResponse> sendPayment(@RequestBody PaymentRequest request);
}