package school.faang.user_service.dto.entity.premium;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum PremiumPeriod {
    ONE_MONTH(1, BigDecimal.valueOf(10)),
    THREE_MONTHS(3, BigDecimal.valueOf(25)),
    ONE_YEAR(12, BigDecimal.valueOf(80));

    private final int months;
    private final BigDecimal price;

    PremiumPeriod(int months, BigDecimal price) {
        this.months = months;
        this.price = price;
    }

    public static PremiumPeriod fromMonths(int months) {
        for (PremiumPeriod period : PremiumPeriod.values()) {
            if (period.getMonths() == months) {
                return period;
            }
        }
        throw new IllegalArgumentException("Неверный период подписки: " + months);
    }
}
