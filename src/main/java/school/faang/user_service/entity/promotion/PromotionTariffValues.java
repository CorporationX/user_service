package school.faang.user_service.entity.promotion;

import lombok.experimental.UtilityClass;
import school.faang.user_service.entity.payment.Currency;

@UtilityClass
public class PromotionTariffValues {
    public static final int NUMBER_OF_VIEWS_STANDARD = 10;
    public static final int NUMBER_OR_VIEWS_PREMIUM = 50;
    public static final int NUMBER_OR_VIEWS_ULTIMATE = 100;

    public static final int AUDIENCE_REACH_STANDARD = 50;
    public static final int AUDIENCE_REACH_PREMIUM = 250;
    public static final int AUDIENCE_REACH_ULTIMATE = 500;

    public static final double COST_STANDARD = 10.0;
    public static final double COST_PREMIUM = 50.0;
    public static final double COST_ULTIMATE = 100.0;

    public static final Currency PROMOTION_CURRENCY = Currency.USD;
}
