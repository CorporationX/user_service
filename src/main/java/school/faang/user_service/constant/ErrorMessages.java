package school.faang.user_service.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ErrorMessages {
    public static final String CANNOT_SUBSCRIBE_OR_UNSUBSCRIBE_TO_SELF = "You cannot subscribe / unsubscribe to yourself.";
    public static final String ALREADY_SUBSCRIBE = "You have been already subscribed to the following user";
    public static final String USER_NOT_SUBSCRIBED = "You are not subscribed to the following user";
}
