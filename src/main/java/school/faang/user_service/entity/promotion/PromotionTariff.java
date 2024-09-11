package school.faang.user_service.entity.promotion;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.payment.Currency;
import school.faang.user_service.exception.promotion.PromotionNotFoundException;

import java.util.Arrays;
import java.util.List;

import static school.faang.user_service.service.promotion.util.PromotionErrorMessages.PROMOTION_NOT_FOUND;

@Getter
public enum PromotionTariff {
    STANDARD(1000, 100, 10.0, Currency.USD),
    PREMIUM(5000, 500, 50.0, Currency.USD),
    ULTIMATE(10_000, 1000, 100.0, Currency.USD);

    PromotionTariff(int numberOfViews, int audienceReach, double cost, Currency currency) {
        this.numberOfViews = numberOfViews;
        this.audienceReach = audienceReach;
        this.coefficient = audienceReach;
        this.cost = cost;
        this.currency = currency;
    }

    private final int numberOfViews;
    private final int audienceReach;
    private final int coefficient;
    private final double cost;
    private final Currency currency;

    public static PromotionTariff fromViews(int numberOfViews) {
        return switch (numberOfViews) {
            case 1000 -> STANDARD;
            case 5000 -> PREMIUM;
            case 10_000 -> ULTIMATE;
            default -> throw new PromotionNotFoundException(PROMOTION_NOT_FOUND, numberOfViews, viewsOption());
        };
    }

    public static List<Integer> viewsOption() {
        return Arrays.stream(PromotionTariff.values())
                .map(PromotionTariff::getNumberOfViews)
                .toList();
    }
}
