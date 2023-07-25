package school.faang.user_service.entity.premium;

import lombok.AllArgsConstructor;
import lombok.Getter;
import school.faang.user_service.exception.DataValidationException;

@Getter
@AllArgsConstructor
public enum PremiumPeriod {

    ONE_MONTH(30, 10),
    THREE_MONTH(90, 25),
    ONE_YEAR(365, 80);

    int days;

    int price;

    public static PremiumPeriod fromDays(int days) {
        switch (days) {
            case 30 -> {
                return ONE_MONTH;
            }
            case 90 -> {
                return THREE_MONTH;
            }
            case 365 -> {
                return ONE_YEAR;
            }
            default -> throw new DataValidationException("Invalid days");
        }
    }
}

