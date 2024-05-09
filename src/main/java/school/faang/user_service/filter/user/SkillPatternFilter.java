package school.faang.user_service.filter.user;

import school.faang.user_service.dto.subscription.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class SkillPatternFilter implements UserFilter {
    @Override
    public boolean isApplicable(SubscriptionUserFilterDto subscriptionUserFilterDto) {
        return subscriptionUserFilterDto.getSkillPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, SubscriptionUserFilterDto subscriptionUserFilterDto) {
        return users.filter(user -> user.getSkills().contains(subscriptionUserFilterDto.getSkillPattern()));
    }
}
