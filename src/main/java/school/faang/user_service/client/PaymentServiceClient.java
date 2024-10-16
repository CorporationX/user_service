package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.model.dto.premium.PaymentRequestDto;
import school.faang.user_service.model.dto.premium.PaymentResponseDto;

@FeignClient(name = "payment-service", url = "${payment-service.host}:${payment-service.port}")
public interface PaymentServiceClient {

    @PostMapping("/api/payment")
    PaymentResponseDto sendPayment(@RequestBody PaymentRequestDto dto);
}