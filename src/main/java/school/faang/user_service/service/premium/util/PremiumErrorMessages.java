package school.faang.user_service.service.premium.util;

public class PremiumErrorMessages {
    public static final String USER_NOT_FOUND = "User with id: %s not found";
    public static final String USER_ALREADY_HAS_PREMIUM = "The user already has a premium subscription before: %s";
    public static final String UNSUCCESSFUL_PAYMENT = "Payment by period: %s for User id: %s unsuccessful";
    public static final String PREMIUM_PERIOD_NOT_FOUND = "Premium period in %s days not found, please select between: %s";
}
