package school.faang.user_service.client.payment_service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "${payment-service.host}:${payment-service.port}")
public interface PaymentServiceClient {

    @PostMapping("api/payment")
    ResponseEntity<PaymentPostPayResponseDto> pay(@RequestBody PaymentPostPayRequestDto requestDto);
}
