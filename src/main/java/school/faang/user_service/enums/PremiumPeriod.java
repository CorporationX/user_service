package school.faang.user_service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public enum PremiumPeriod {
    MONTHLY(1, new BigDecimal("10.00")),
    QUARTERLY(3, new BigDecimal("25.00")),
    YEARLY(12, new BigDecimal("80.00"));

    private final int months;
    private final BigDecimal amount;
}
