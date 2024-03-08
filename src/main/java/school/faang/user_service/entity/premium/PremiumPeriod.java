package school.faang.user_service.entity.premium;

import lombok.Getter;
import school.faang.user_service.exception.DataValidationException;

import java.util.Arrays;

@Getter
public enum PremiumPeriod {

    ONE_MONTH(30, 10),
    THREE_MONTH(90, 25),
    ONE_YEAR(365, 80);


    private final int days;
    private final int price;

    PremiumPeriod(int days, int price) {
        this.days = days;
        this.price = price;
    }

    public static PremiumPeriod fromDays(int days) {
        return Arrays.stream(values())
                .filter(premiumPeriod -> premiumPeriod.getDays() == days)
                .findFirst()
                .orElseThrow(() -> new DataValidationException("Invalid argument - days: " + days));
    }


}
