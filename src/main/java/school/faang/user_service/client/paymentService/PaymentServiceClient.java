package school.faang.user_service.client.paymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.client.paymentService.model.PaymentRequest;
import school.faang.user_service.client.paymentService.model.PaymentResponse;
import school.faang.user_service.client.paymentService.model.PaymentStatus;
import school.faang.user_service.exception.payment.PaymentException;

import java.math.BigDecimal;
import java.util.Random;

/**
 * @author Evgenii Malkov
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceClient {
    private final PaymentServiceFeignClient client;
    private final CurrencyService currencyService;
    @Value("${payment-service.currency:USD}")
    private String currency;
    @Value("${payment-service.dbCurrency:USD}")
    private String dbCurrency;

    public PaymentResponse sendPayment(long entityId, BigDecimal price) {
        Random random = new Random();
        long paymentNumber = random.nextLong(0, Long.MAX_VALUE);

        if (!this.currency.equals(this.dbCurrency)) {
            price = currencyService.convertPriceFromDbCurrency(price, this.currency);
        }

        PaymentRequest request = new PaymentRequest(paymentNumber, price, currency);
        PaymentResponse response = sendPayment(request);
        if (!PaymentStatus.SUCCESS.equals(response.status())) {
            log.error("Error during payment execution. Entity: {}, paymentNumber: {}",
                    entityId, response.paymentNumber());
            throw new PaymentException("Error during payment execution");
        }
        log.info(response.message());
        savePaymentInfo(response);
        return response;
    }

    private void savePaymentInfo(PaymentResponse paymentResponse) {
        log.info("Сохранили необходимую инфу о платеже: {}...", paymentResponse.paymentNumber());
    }

    private PaymentResponse sendPayment(PaymentRequest request) {
        try {
            return client.sendPayment(request);
        } catch (Exception e) {
            log.error("Failed payment: {}", request.paymentNumber());
            log.error(e.getMessage());
            throw new PaymentException("Failed payment: " + request.paymentNumber(), e);
        }
    }
}
