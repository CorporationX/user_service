package school.faang.user_service.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.exception.DataValidationException;

@Getter
@RequiredArgsConstructor
public enum PremiumPeriod {
    MONTH (30, 10),
    THREEMONTH (90, 25),
    YEAR (356, 80);

    private final int days;
    private final int cost;

    public static PremiumPeriod fromDays(int days) {
        return switch (days) {
            case 30 -> PremiumPeriod.MONTH;
            case 90 -> PremiumPeriod.THREEMONTH;
            case 356 -> PremiumPeriod.YEAR;
            default -> throw new DataValidationException("Передан неверный период подписки");
        };
    }
}
