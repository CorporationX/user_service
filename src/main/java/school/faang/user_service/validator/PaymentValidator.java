package school.faang.user_service.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.client.PaymentResponse;
import school.faang.user_service.dto.client.PaymentStatus;
import school.faang.user_service.exception.PaymentFailedException;

@Slf4j
@Component
public class PaymentValidator {

    public void verifyPayment(ResponseEntity<PaymentResponse> responseEntity) {
        PaymentResponse response = checkNotNull(responseEntity.getBody());
        int verificationCode = response.verificationCode();

        if (isSuccessPayment(responseEntity.getStatusCode(), response, verificationCode)) {
            log.info("Successfully processed payment request: {}", verificationCode);
        } else {
            String message = "Failed to process payment request: %d".formatted(verificationCode);
            log.info(message);
            throw new PaymentFailedException(message);
        }
    }

    private PaymentResponse checkNotNull(PaymentResponse responseBody) {
        if (responseBody == null) {
            String message = "Response is null";
            log.info(message);
            throw new PaymentFailedException(message);
        }
        return responseBody;
    }

    private boolean isSuccessPayment(HttpStatusCode code, PaymentResponse response, int verificationCode) {
        return response.status() == PaymentStatus.SUCCESS &&
                code.is2xxSuccessful() &&
                verificationCode != 0;
    }
}
