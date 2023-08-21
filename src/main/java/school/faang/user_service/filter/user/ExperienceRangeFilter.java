package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;
@Component
public class ExperienceRangeFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter.getExperienceMin() > 0 && filter.getExperienceMax() > filter.getExperienceMin();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filterDto) {
        return users.filter(user -> {
                    int userExperience = user.getExperience() != null ? user.getExperience() : 0;
                    return userExperience >= filterDto.getExperienceMin() && userExperience <= filterDto.getExperienceMax();
                }
        );
    }
}
