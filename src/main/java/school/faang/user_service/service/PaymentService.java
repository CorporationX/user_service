package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.dto.PaymentRequest;
import school.faang.user_service.dto.PaymentResponse;

@Service
@RequiredArgsConstructor
public class PaymentService {
    @Value("${payment-service.host}")
    private String hostUrl;
    @Value("${payment-service.port}")
    private String hostPort;
    private final RestTemplate restTemplate;

    public PaymentResponse initPayment(PaymentRequest paymentRequest) {
        return restTemplate.postForObject(
                "http://" + hostUrl + ":" + hostPort + "/api/payment",
                paymentRequest,
                PaymentResponse.class);
    }

    public long getNextPaymentId() {
        return System.currentTimeMillis();
    }
}
