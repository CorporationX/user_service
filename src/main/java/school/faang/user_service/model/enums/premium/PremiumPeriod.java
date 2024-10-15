package school.faang.user_service.model.enums.premium;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum PremiumPeriod {
    MONTH(30, new BigDecimal(10)),
    THREE_MONTHS(90, new BigDecimal(25)),
    YEAR(365, new BigDecimal(80));

    private static final Map<Integer, PremiumPeriod> DAYS_TO_PERIOD_MAP = new HashMap<>();
    private final int days;
    private final BigDecimal price;

    static {
        for (var period : PremiumPeriod.values()) {
            DAYS_TO_PERIOD_MAP.put(period.days, period);
        }
    }

    public static PremiumPeriod fromDays(int days) {
        return Optional.ofNullable(DAYS_TO_PERIOD_MAP.get(days))
                .orElseThrow(() -> new IllegalArgumentException(
                        "No premium period found for %d days".formatted(days)));
    }
}