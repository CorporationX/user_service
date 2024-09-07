package school.faang.user_service.entity.premium;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import school.faang.user_service.exception.DataValidationException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PremiumPeriodTest {

    private static final PremiumPeriod PREMIUM_PERIOD = PremiumPeriod.MONTH;

    @Test
    void getPrice_USD() {
        int correctPrice = 10;
        Currency currency = Currency.USD;

        BigDecimal price = PREMIUM_PERIOD.getPrice(currency);

        assertNotNull(price);
        assertEquals(correctPrice, price.intValue());
    }

    @Test
    void getPrice_EUR() {
        int correctPrice = 9;
        Currency currency = Currency.EUR;

        BigDecimal price = PREMIUM_PERIOD.getPrice(currency);

        assertNotNull(price);
        assertEquals(correctPrice, price.intValue());
    }

    @Test
    void fromDays_WithValidDays() {
        int days = 30;

        PremiumPeriod premiumPeriod = PremiumPeriod.fromDays(days);

        assertEquals(PREMIUM_PERIOD, premiumPeriod);
    }

    @ParameterizedTest
    @ValueSource(ints = {15})
    @NullSource
    void fromDays_WithInvalidDays(Integer days) {
        String message = "Days not in premium period: %d".formatted(days);

        var exception = assertThrows(DataValidationException.class,
                () -> PremiumPeriod.fromDays(days)
        );

        assertEquals(message, exception.getMessage());
    }
}