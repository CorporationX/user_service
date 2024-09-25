package school.faang.user_service.entity.premium;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.exception.DataValidationException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum PremiumPeriod {

    MONTH(30,
            new BigDecimal[]{new BigDecimal(10), new BigDecimal(9)}),
    THREE_MONTH(90,
            new BigDecimal[]{new BigDecimal(25), new BigDecimal(22)}),
    YEAR(365,
            new BigDecimal[]{new BigDecimal(80), new BigDecimal(72)});

    private static final Map<Integer, PremiumPeriod> daysByPremiumPeriod;

    private final int days;
    private final BigDecimal[] prices;

    static {
        PremiumPeriod[] premiumPeriods = PremiumPeriod.values();
        daysByPremiumPeriod = new HashMap<>(premiumPeriods.length);
        Arrays.stream(premiumPeriods).forEach(
                period -> daysByPremiumPeriod.put(period.days, period));
    }

    public BigDecimal getPrice(Currency currency) {
        return prices[currency.ordinal()];
    }

    public static PremiumPeriod fromDays(Integer days) {
        validate(days);
        return daysByPremiumPeriod.get(days);
    }

    private static void validate(Integer days) {
        if (!daysByPremiumPeriod.containsKey(days)) {
            log.warn("Days not in premium period: {}", days);
            throw new DataValidationException("Days not in premium period: %d".formatted(days));
        }
    }
}
