package school.faang.user_service.entity.premium;

import lombok.AllArgsConstructor;
import lombok.Getter;
import school.faang.user_service.entity.transaction.Payable;
import school.faang.user_service.entity.transaction.TransactionPurpose;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum PremiumPeriod implements Payable {
    MONTHLY("Monthly", new BigDecimal("10.00"), Currency.getInstance("USD"), 30),
    QUARTERLY("Quarterly", new BigDecimal("25.00"), Currency.getInstance("USD"), 90),
    ANNUALLY("Annual", new BigDecimal("80.00"), Currency.getInstance("USD"), 365);

    private final String name;
    private final BigDecimal price;
    private final Currency currency;
    private final Integer days;
    private final TransactionPurpose purpose = TransactionPurpose.PREMIUM;

    public static PremiumPeriod fromDays(Integer days) {
      return Arrays.stream(values())
              .filter(period -> Objects.equals(period.getDays(), days))
              .findFirst()
              .orElseThrow(() -> new IllegalArgumentException("No such premium period for " + days + " days"));
    }
}
