package school.faang.user_service.filter.user_filter;

import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public interface UserFilter {
    boolean isApplicable(SubscriptionUserFilterDto filters);

    void apply(Stream<User> users, SubscriptionUserFilterDto subscriptionUserFilterDto);
}
