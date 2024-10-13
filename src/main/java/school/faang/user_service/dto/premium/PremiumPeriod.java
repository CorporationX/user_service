package school.faang.user_service.dto.premium;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PremiumPeriod {
    ONE_MONTH(30, 10L),
    THREE_MONTHS(90, 25L),
    ONE_YEAR(365, 80L);

    private final int days;
    private final long price;

    public static PremiumPeriod getByDays(int days) {
        for (PremiumPeriod period : PremiumPeriod.values()) {
            if (period.days == days) {
                return period;
            }
        }
        throw new IllegalArgumentException("No PremiumPeriod found for " + days + " days");
    }
}

