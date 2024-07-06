package school.faang.user_service.filter.userFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Component
public class UserExperienceMaxFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto userFilterDto) {
        return userFilterDto.getExperienceMax() != null;
    }

    @Override
    public List<User> apply(List<User> users, UserFilterDto userFilter) {
        return users.stream().filter(user -> user.getExperience() <= userFilter.getExperienceMax()).toList();
    }
}
