package school.faang.user_service.filter.user_filter;

import school.faang.user_service.dto.subscription.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class PhonePatternFilter implements UserFilter {
    @Override
    public boolean isApplicable(SubscriptionUserFilterDto subscriptionUserFilterDto) {
        return subscriptionUserFilterDto.getPhonePattern() != null;
    }

    @Override
    public void apply(Stream<User> users, SubscriptionUserFilterDto subscriptionUserFilterDto) {
        users.filter(user -> user.getPhone().matches(subscriptionUserFilterDto.getPhonePattern()));
    }
}
