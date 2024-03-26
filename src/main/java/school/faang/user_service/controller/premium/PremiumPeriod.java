package school.faang.user_service.controller.premium;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.NoSuchElementException;


@Getter
@RequiredArgsConstructor
public enum PremiumPeriod {
    BASE(30, BigDecimal.valueOf(10)),
    STANDARD(90, BigDecimal.valueOf(25)),
    LEGEND(360, BigDecimal.valueOf(80));
    private final int days;
    private final BigDecimal cost;

    public static PremiumPeriod fromDays(int days){
        return Arrays.stream(PremiumPeriod.values())
                .filter(premiumPeriod -> premiumPeriod.getDays() == days)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Subscription with that period is not found"));
    }
}
