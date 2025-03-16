package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.dto.PaymentRequestDto;
import school.faang.user_service.dto.PaymentResponseDto;
import school.faang.user_service.dto.PaymentStatus;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    @Value("${payment-service.host}")
    private String hostUrl;
    @Value("${payment-service.port}")
    private String hostPort;
    private final RestTemplate restTemplate;

    public PaymentResponseDto initPayment(PaymentRequestDto paymentRequestDto) {
        try {
            return restTemplate.postForObject(
                    "http://" + hostUrl + ":" + hostPort + "/api/payment",
                    paymentRequestDto,
                    PaymentResponseDto.class);
        } catch (RestClientException e) {
            String message = e.getMessage();
            long paymentNumber = paymentRequestDto.paymentNumber();

            log.error("Failed to init payment for {}: {}", paymentNumber, message);

            return new PaymentResponseDto(
                    PaymentStatus.FALSE,
                    -1,
                    paymentNumber,
                    paymentRequestDto.amount(),
                    paymentRequestDto.currency(),
                    message
            );
        }
    }

    public long getNextPaymentId() {
        return Math.abs(UUID.randomUUID().getMostSignificantBits());
    }
}
