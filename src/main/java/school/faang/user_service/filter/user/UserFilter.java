package school.faang.user_service.filter.user;

import school.faang.user_service.dto.subscription.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public interface UserFilter {
    boolean isApplicable(SubscriptionUserFilterDto filters);

    Stream<User> apply(Stream<User> users, SubscriptionUserFilterDto subscriptionUserFilterDto);
}
