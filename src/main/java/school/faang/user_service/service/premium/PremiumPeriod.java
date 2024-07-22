package school.faang.user_service.service.premium;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum PremiumPeriod {
    MONTH(30, new BigDecimal(10)),
    THREE_MONTH(90, new BigDecimal(25)),
    YEAR(365, new BigDecimal(80));

    private final int days;
    private final BigDecimal price;

    private static final Map<Integer, PremiumPeriod> DAYS_TO_PERIOD_MAP = new HashMap<>();

    static {
        for (PremiumPeriod period : values()) {
            DAYS_TO_PERIOD_MAP.put(period.getDays(), period);
        }
    }

    public static PremiumPeriod getPremiumPeriod(int days) {
        return Optional.ofNullable(DAYS_TO_PERIOD_MAP.get(days))
                .orElseThrow(() -> new IllegalArgumentException("No premium period found for days: " + days));
    }
}
