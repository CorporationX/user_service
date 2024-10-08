package school.faang.user_service.client.payment;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import school.faang.user_service.dto.payment.PaymentRequestDto;
import school.faang.user_service.dto.payment.PaymentResponseDto;

@FeignClient(name = "payment-service", url = "${payment-service.url}")
public interface PaymentServiceClient {

    @PostMapping("/payment")
    PaymentResponseDto sendPayment(PaymentRequestDto requestDto);
}
