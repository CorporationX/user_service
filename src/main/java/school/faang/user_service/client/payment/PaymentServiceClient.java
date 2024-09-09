package school.faang.user_service.client.payment;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import school.faang.user_service.dto.payment.PaymentRequest;
import school.faang.user_service.dto.payment.PaymentResponse;

@FeignClient(name = "payment-service", url = "${payment-service.host}:${payment-service.port}")
public interface PaymentServiceClient {

    @PostMapping("/api/payment")
    PaymentResponse sendPayment(PaymentRequest requestDto);
}
