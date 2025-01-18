package school.faang.user_service.entity.premium;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PremiumPeriod {
    MONTH(30, 10),
    THREE_MONTHS(90, 25),
    YEAR(365, 80);

    private final int days;
    private final int price;

    public static PremiumPeriod fromDays(int days) {
        for (PremiumPeriod period : PremiumPeriod.values()) {
            if (period.getDays() == days) {
                return period;
            }
        }
        throw new IllegalArgumentException("No PremiumPeriod found for " + days + " days");
    }
}
