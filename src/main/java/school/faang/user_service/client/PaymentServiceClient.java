package school.faang.user_service.client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import school.faang.user_service.dto.payment.PaymentRequestDto;
import school.faang.user_service.dto.payment.PaymentResponseDto;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

@FeignClient(
        name = "${services.payment-service.name}",
        configuration = FeignConfig.class
)
@Retryable(
        retryFor = { SocketTimeoutException.class,
                ConnectException.class,
                FeignException.ServiceUnavailable.class,
                FeignException.GatewayTimeout.class },
        backoff = @Backoff(delay = 1000, multiplier = 2)
)
public interface PaymentServiceClient {

    @PostMapping("/api/payment")
    PaymentResponseDto buyPremium(@RequestBody PaymentRequestDto request);
}
