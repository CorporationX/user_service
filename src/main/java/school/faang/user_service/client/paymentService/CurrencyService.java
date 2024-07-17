package school.faang.user_service.client.paymentService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Currency;
import school.faang.user_service.exception.payment.PaymentException;
import school.faang.user_service.repository.CurrencyRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Evgenii Malkov
 */
@Component
@RequiredArgsConstructor
public class CurrencyService {
    private final CurrencyRepository currencyRepository;

    public BigDecimal convertPriceFromDbCurrency(BigDecimal price, String targetCurrency) {
        Currency paymentCurrency = findCurrencyByCode(targetCurrency);
        return price.multiply(paymentCurrency.getExchangeRate())
                .setScale(2, RoundingMode.HALF_UP);
    }

    public Currency findCurrencyByCode(String code) {
        return currencyRepository.findById(code)
                .orElseThrow(() -> new PaymentException("Not found data for: " + code));
    }
}
