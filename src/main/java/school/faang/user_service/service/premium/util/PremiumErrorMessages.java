package school.faang.user_service.service.premium.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PremiumErrorMessages {
    public static final String USER_NOT_FOUND_PREMIUM = "User with id: %s not found, when buying premium";
    public static final String USER_NOT_FOUND_PROMOTION = "User with id: %s not found, when buying promotion";
    public static final String USER_ALREADY_HAS_PREMIUM = "The user with id: %s already has a premium subscription before: %s";
    public static final String UNSUCCESSFUL_PREMIUM_PAYMENT = "Payment by period: %s for User id: %s unsuccessful";
    public static final String PREMIUM_PERIOD_NOT_FOUND = "Premium period in %s days not found, please select among: %s";
}
