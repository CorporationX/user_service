package school.faang.user_service.service.promotion.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PromotionErrorMessages {
    public static final String PROMOTION_NOT_FOUND = "Promotion tariff of %s views not found, please select between: %s";
    public static final String USER_ALREADY_HAS_PROMOTION = "User with id: %s already has promotion, %S views left";
    public static final String UNSUCCESSFUL_USER_PROMOTION_PAYMENT = "Payment by: %s views promotion for User id: %s unsuccessful. Response message: %s";
    public static final String UNSUCCESSFUL_EVENT_PROMOTION_PAYMENT = "Payment by: %s views promotion for Event id: %s unsuccessful. Response message: %s";
    public static final String EVENT_NOT_FOUND_PROMOTION = "Event with id: %s not found, when buying promotion";
    public static final String EVENT_ALREADY_HAS_PROMOTION = "Event with id: %s already has promotion, %S views left";
    public static final String USER_NOT_OWNER_OF_EVENT = "User with id: %s not owner of event with id: %s";
}
