package school.faang.user_service.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum TariffPlan {
    MONTHLY(BigDecimal.TEN, 1),
    THREE_MONTHS(new BigDecimal(25), 3),
    YEARLY(new BigDecimal(80), 12);

    private final static Map<String, TariffPlan> TARIFF_PLANS = Arrays.stream(TariffPlan.values())
        .collect(Collectors.toMap(Enum::name, Function.identity()));

    private final BigDecimal price;

    private final int durationMonths;

    public LocalDateTime getEndDate() {
        LocalDateTime now = LocalDateTime.now();
        return now.plusMonths(durationMonths);
    }
}
