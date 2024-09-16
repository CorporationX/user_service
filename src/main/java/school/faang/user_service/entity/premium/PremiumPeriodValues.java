package school.faang.user_service.entity.premium;

import lombok.experimental.UtilityClass;
import school.faang.user_service.entity.payment.Currency;

@UtilityClass
public class PremiumPeriodValues {
    public static final double COST_MONTH = 10.0;
    public static final double COST_THREE_MOTH = 25.0;
    public static final double COST_YEAR = 80.0;

    public static final int DAYS_MONTH = 31;
    public static final int DAYS_THREE_MOTH = 62;
    public static final int DAYS_YEAR = 365;

    public static final Currency PREMIUM_CURRENCY = Currency.USD;

}
