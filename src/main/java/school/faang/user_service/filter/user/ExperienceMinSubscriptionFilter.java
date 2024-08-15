package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.userSubscriptionDto.UserSubscriptionFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class ExperienceMinSubscriptionFilter implements UserSubscriptionFilter {
    @Override
    public boolean isApplication(UserSubscriptionFilterDto userSubscriptionFilterDto) {
        return userSubscriptionFilterDto.getExperienceMin() > 0;
    }

    @Override
    public Stream<User> apply(Stream<User> userStream, UserSubscriptionFilterDto userSubscriptionFilterDto) {
        return userStream.filter(user -> user.getExperience() >= userSubscriptionFilterDto.getExperienceMin());
    }
}
