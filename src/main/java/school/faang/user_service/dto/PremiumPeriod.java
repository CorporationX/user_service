package school.faang.user_service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.exception.IllegalSubscriptionException;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum PremiumPeriod {
    MONTHLY(30, 10),
    QUARTER(90, 25),
    YEARLY(360, 80);

    private final int days;
    private final int cost;

    public static PremiumPeriod fromDays(int days) {
        return Arrays.stream(PremiumPeriod.values())
            .filter(premiumPeriod -> premiumPeriod.days == days)
            .findFirst()
            .orElseThrow(() -> new IllegalSubscriptionException(days));
    }
}
