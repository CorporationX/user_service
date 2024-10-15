package school.faang.user_service.model.enums;

import lombok.Getter;
import school.faang.user_service.exception.DataValidationException;

@Getter
public enum PremiumPeriod {
    ONE_MONTH(30, 10),
    THREE_MONTHS(90, 25),
    ONE_YEAR(365, 80);

    private final int days;
    private final int price;

    PremiumPeriod(int days, int price) {
        this.days = days;
        this.price = price;
    }

    public static PremiumPeriod fromDays(int days) {
        for (PremiumPeriod period : values()) {
            if (period.days == days) {
                return period;
            }
        }
        throw new DataValidationException("Invalid premium period");
    }
}
