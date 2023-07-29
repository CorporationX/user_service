package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserExperienceMaxFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto userFilterDto) {
        return userFilterDto.getExperienceMax() > 0;
    }

    @Override
    public Stream<school.faang.user_service.entity.User> apply(Stream<User> users, UserFilterDto userFilterDto) {
        return users.filter(user -> user.getExperience() <= userFilterDto.getExperienceMax());
    }
}
