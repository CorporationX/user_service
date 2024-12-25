package school.faang.user_service.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.exceptions.DataValidationException;

@RequiredArgsConstructor
@Getter
public enum PremiumPeriod {
    ONE_MONTH(30, 9.99),
    THREE_MONTHS(90, 24.99),
    ONE_YEAR(365, 79.99);

    private final int days;
    private final double price;

    public static PremiumPeriod fromDays(int days) {
        for (PremiumPeriod period : values()) {
            if (period.getDays() == days) {
                return period;
            }
        }
        throw new DataValidationException(String.format("No premium period for %d days", days));
    }
}