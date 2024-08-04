package school.faang.user_service.model.premium;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.exception.premium.PremiumIllegalArgumentException;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * @author Evgenii Malkov
 */
@Getter
@RequiredArgsConstructor
public enum PremiumPeriod {
    MONTH(30, new BigDecimal(10)),
    QUARTER(90, new BigDecimal(25)),
    YEAR(365, new BigDecimal(80));

    private final int days;
    private final BigDecimal price;

    public static PremiumPeriod fromDays(int days) {
        return Arrays.stream(PremiumPeriod.values())
                .filter((period -> period.getDays() == days))
                .findFirst()
                .orElseThrow(() -> new PremiumIllegalArgumentException(
                        "A period with this number of days was not found: " + days));
    }
}
