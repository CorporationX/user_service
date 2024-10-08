package school.faang.user_service.entity.promotion;

import lombok.Getter;
import school.faang.user_service.entity.payment.Currency;
import school.faang.user_service.exception.promotion.PromotionNotFoundException;

import java.util.Arrays;
import java.util.List;

import static school.faang.user_service.entity.promotion.PromotionTariffValues.AUDIENCE_REACH_PREMIUM;
import static school.faang.user_service.entity.promotion.PromotionTariffValues.AUDIENCE_REACH_STANDARD;
import static school.faang.user_service.entity.promotion.PromotionTariffValues.AUDIENCE_REACH_ULTIMATE;
import static school.faang.user_service.entity.promotion.PromotionTariffValues.COST_PREMIUM;
import static school.faang.user_service.entity.promotion.PromotionTariffValues.COST_STANDARD;
import static school.faang.user_service.entity.promotion.PromotionTariffValues.COST_ULTIMATE;
import static school.faang.user_service.entity.promotion.PromotionTariffValues.PROMOTION_CURRENCY;
import static school.faang.user_service.entity.promotion.PromotionTariffValues.NUMBER_OF_VIEWS_STANDARD;
import static school.faang.user_service.entity.promotion.PromotionTariffValues.NUMBER_OR_VIEWS_PREMIUM;
import static school.faang.user_service.entity.promotion.PromotionTariffValues.NUMBER_OR_VIEWS_ULTIMATE;
import static school.faang.user_service.service.promotion.util.PromotionErrorMessages.PROMOTION_NOT_FOUND;

@Getter
public enum PromotionTariff {
    STANDARD(NUMBER_OF_VIEWS_STANDARD, AUDIENCE_REACH_STANDARD, COST_STANDARD, PROMOTION_CURRENCY),
    PREMIUM(NUMBER_OR_VIEWS_PREMIUM, AUDIENCE_REACH_PREMIUM, COST_PREMIUM, PROMOTION_CURRENCY),
    ULTIMATE(NUMBER_OR_VIEWS_ULTIMATE, AUDIENCE_REACH_ULTIMATE, COST_ULTIMATE, PROMOTION_CURRENCY);

    PromotionTariff(int numberOfViews, int audienceReach, double cost, Currency currency) {
        this.numberOfViews = numberOfViews;
        this.audienceReach = audienceReach;
        this.coefficient = audienceReach * numberOfViews;
        this.cost = cost;
        this.currency = currency;
    }

    private final int numberOfViews;
    private final int audienceReach;
    private final double coefficient;
    private final double cost;
    private final Currency currency;

    public static PromotionTariff fromViews(int numberOfViews) {
        return switch (numberOfViews) {
            case NUMBER_OF_VIEWS_STANDARD -> STANDARD;
            case NUMBER_OR_VIEWS_PREMIUM -> PREMIUM;
            case NUMBER_OR_VIEWS_ULTIMATE -> ULTIMATE;
            default -> throw new PromotionNotFoundException(PROMOTION_NOT_FOUND, numberOfViews, viewsOption());
        };
    }

    public static List<Integer> viewsOption() {
        return Arrays.stream(PromotionTariff.values())
                .map(PromotionTariff::getNumberOfViews)
                .toList();
    }
}
