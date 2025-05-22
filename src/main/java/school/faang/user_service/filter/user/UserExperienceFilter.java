package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserExperienceFilter  implements UserFilter {
    @Override
    public boolean isAvailable(UserFilterDto filter) {
        return filter.getExperienceMin() < filter.getExperienceMax();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filter) {
        return users.filter(user -> isRelevant(user.getExperience(), filter));
    }

    private boolean isRelevant(Integer userExperience, UserFilterDto filter) {
        if (userExperience == null) {
            return false;
        }
        return filter.getExperienceMin() <= userExperience &&
                userExperience < filter.getExperienceMax();
    }
}
