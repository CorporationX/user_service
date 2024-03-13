package school.faang.user_service.entity.premium;

import lombok.AllArgsConstructor;
import lombok.Getter;
import school.faang.user_service.exception.EntityNotFoundException;

import java.math.BigDecimal;
import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum PremiumPeriod {

    MONTH(30, BigDecimal.valueOf(10)),
    THREE_MONTH(90, BigDecimal.valueOf(25)),
    YEAR(365, BigDecimal.valueOf(80));

    private final int days;
    private final BigDecimal price;

    public static PremiumPeriod fromDays(int days) {
        return Arrays.stream(values())
                .filter(period -> period.days == days)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(String.format("Premium period with %d days does not exist", days)));
    }
}
