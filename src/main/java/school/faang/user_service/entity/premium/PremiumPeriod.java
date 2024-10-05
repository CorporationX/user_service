package school.faang.user_service.entity.premium;

import lombok.AllArgsConstructor;
import lombok.Getter;
import school.faang.user_service.entity.payment.Currency;
import school.faang.user_service.exception.premium.PremiumNotFoundException;

import java.util.Arrays;
import java.util.List;

import static school.faang.user_service.entity.premium.PremiumPeriodValues.COST_MONTH;
import static school.faang.user_service.entity.premium.PremiumPeriodValues.COST_THREE_MOTH;
import static school.faang.user_service.entity.premium.PremiumPeriodValues.COST_YEAR;
import static school.faang.user_service.entity.premium.PremiumPeriodValues.DAYS_MONTH;
import static school.faang.user_service.entity.premium.PremiumPeriodValues.DAYS_THREE_MOTH;
import static school.faang.user_service.entity.premium.PremiumPeriodValues.DAYS_YEAR;
import static school.faang.user_service.entity.premium.PremiumPeriodValues.PREMIUM_CURRENCY;
import static school.faang.user_service.service.premium.util.PremiumErrorMessages.PREMIUM_PERIOD_NOT_FOUND;

@Getter
@AllArgsConstructor
public enum PremiumPeriod {
    MONTH(COST_MONTH, DAYS_MONTH, PREMIUM_CURRENCY),
    THREE_MONTH(COST_THREE_MOTH, DAYS_THREE_MOTH, PREMIUM_CURRENCY),
    YEAR(COST_YEAR, DAYS_YEAR, PREMIUM_CURRENCY);

    private final double cost;
    private final int days;
    private final Currency currency;

    public static PremiumPeriod fromDays(int days) {
        return switch (days) {
            case DAYS_MONTH -> MONTH;
            case DAYS_THREE_MOTH -> THREE_MONTH;
            case DAYS_YEAR -> YEAR;
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
