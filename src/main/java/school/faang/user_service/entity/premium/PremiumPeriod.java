package school.faang.user_service.entity.premium;

import lombok.AllArgsConstructor;
import lombok.Getter;
import school.faang.user_service.entity.payment.Currency;
import school.faang.user_service.exception.premium.PremiumNotFoundException;

import java.util.Arrays;
import java.util.List;

import static school.faang.user_service.service.premium.util.PremiumErrorMessages.PREMIUM_PERIOD_NOT_FOUND;

@Getter
@AllArgsConstructor
public enum PremiumPeriod {
    MONTH(10.0, 31, Currency.USD),
    THREE_MONTH(25.0, 62, Currency.USD),
    YEAR(80.0, 365, Currency.USD);

    private final double cost;
    private final int days;
    private final Currency currency;

    public static PremiumPeriod fromDays(int days) {
        return switch (days) {
            case 31 -> MONTH;
            case 62 -> THREE_MONTH;
            case 365 -> YEAR;
            default -> throw new PremiumNotFoundException(PREMIUM_PERIOD_NOT_FOUND, days, PremiumPeriod.daysOptions());
        };
    }

    public static List<Integer> daysOptions() {
        return Arrays
                .stream(PremiumPeriod.values())
                .map(PremiumPeriod::getDays)
                .toList();
    }
}
