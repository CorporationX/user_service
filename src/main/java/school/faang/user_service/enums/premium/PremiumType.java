package school.faang.user_service.enums.premium;

import lombok.AllArgsConstructor;
import lombok.Getter;
import school.faang.user_service.exception.premium.PremiumTypeNotFoundException;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public enum PremiumType {
    ONE_MONTH(new BigDecimal("10"), 1),
    THREE_MONTHS(new BigDecimal("27"), 3),
    ONE_YEAR(new BigDecimal("90"), 12);

    private BigDecimal priceInDollars;
    private int months;

    public static PremiumType getByDuration(int months) {
        for (PremiumType type : PremiumType.values()) {
            if (type.getMonths() == months) {
                return type;
            }
        }
        throw new PremiumTypeNotFoundException("No PremiumType found for the given duration: " + months);
    }
}
