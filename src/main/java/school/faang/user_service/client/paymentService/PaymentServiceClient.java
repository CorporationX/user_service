package school.faang.user_service.client.paymentService;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.client.paymentService.model.Currency;
import school.faang.user_service.client.paymentService.model.PaymentRequest;
import school.faang.user_service.exception.payment.PaymentException;
import school.faang.user_service.kafka.producer.PaymentRequestProducer;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * @author Evgenii Malkov
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceClient {

    private final CurrencyService currencyService;
    private final PaymentRequestProducer requestProducer;
    private final ObjectMapper objectMapper;
    @Value("${payment-service.currency:USD}")
    private String currency;
    @Value("${payment-service.dbCurrency:USD}")
    private String dbCurrency;

    public long sendPaymentRequest(long entityId, BigDecimal price) {
        if (!this.currency.equals(this.dbCurrency)) {
            price = currencyService.convertPriceFromDbCurrency(price, this.currency);
        }
        long requestId = generateRequestId(entityId, price);
        PaymentRequest request = new PaymentRequest(requestId, price, Currency.valueOf(currency));
        sendPaymentRequest(request);
        return requestId;
    }

    private void sendPaymentRequest(PaymentRequest request) {
        try {
            requestProducer.sendPaymentRequest(objectMapper.writeValueAsString(request));
        } catch (Exception e) {
            log.error("Failed payment: {}", request.requestId());
            log.error(e.getMessage());
            throw new PaymentException("Failed payment: " + request.requestId(), e);
        }
    }

    private long generateRequestId(long entityId, BigDecimal price) {
        long priceInCents = price.movePointRight(2).longValue();
        long currentTimeMillis = Instant.now().toEpochMilli();
        String paymentIdString = String.format("%s-%s-%s", entityId, priceInCents, currentTimeMillis);
        return paymentIdString.hashCode() & 0xffffffffL;
    }
}
