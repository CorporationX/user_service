package school.faang.user_service.entity.premium;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PremiumPeriod {
    MONTHLY(30, "Месяц"),
    QUARTERLY(90, "3 месяца"),
    ANNUAL(365, "Год");

    private final int days;
    private final String name;

    PremiumPeriod(int days, String name) {
        this.days = days;
        this.name = name;
    }

    public static PremiumPeriod fromDays(int days) {
        return Arrays.stream(values())
                .filter(period -> period.getDays() == days)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Недопустимое количество дней: " + days));
    }
}
