package school.faang.user_service.service.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.client.FeignUserInterceptor;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.payment.PaymentRequestDto;
import school.faang.user_service.dto.payment.PaymentResponseDto;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentServiceClient paymentServiceClient;

    public PaymentResponseDto buyItem(PaymentRequestDto request) {
        log.info("Attempting to buy premium for transaction: {}", request.getPaymentNumber());
        PaymentResponseDto response = paymentServiceClient.buyPremium(request);
        validateAndProcessResponse(request, response);
        return response;
    }

    private void validateAndProcessResponse(PaymentRequestDto request, PaymentResponseDto response) {
        if (!response.getAmount().equals(request.getAmount()) ||
        !response.getCurrency().equals(request.getCurrency())||
        !response.getPaymentNumber().equals(request.getPaymentNumber())) {
            log.error("Payment response does not match request. Request: {}, Response: {}", request, response);
            throw new RuntimeException("Payment response does not match request");
        }
    }
}
