package school.faang.user_service.entity.premium;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PremiumPeriod {

    ONE_MONTH(30, 10),
    THREE_MONTHS(90, 25),
    YEAR(365, 80);

    private final int days;
    private final int price;

    public static PremiumPeriod fromDays(int days) {
        return switch (days) {
            case 30 -> ONE_MONTH;
            case 90 -> THREE_MONTHS;
            case 365 -> YEAR;
            default -> throw new IllegalArgumentException("Incorrect number of days!");
        };
    }
}
