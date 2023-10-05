package school.faang.user_service.entity.premium;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PremiumPeriod {
    ONE_MONTH(30, 10),
    THREE_MONTHS(90, 25),
    ONE_YEAR(365, 80);

    private final int days;
    private final double price;

    public static PremiumPeriod fromDays(int days) {
        var periods = values();
        for (int i = periods.length - 1; i >= 0; i--) {
            if (periods[i].days <= days) {
                return periods[i];
            }
        }
        throw new IllegalArgumentException("The provided period is too small");
    }
}