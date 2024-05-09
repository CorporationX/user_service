package school.faang.user_service.filter.user;

import school.faang.user_service.dto.subscription.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class ContactPatternFilter implements UserFilter {
    @Override
    public boolean isApplicable(SubscriptionUserFilterDto filters) {
        return filters.getContactPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, SubscriptionUserFilterDto subscriptionUserFilterDto) {
        return users.filter(user -> user.getContacts().contains(subscriptionUserFilterDto.getContactPattern()));
    }
}
