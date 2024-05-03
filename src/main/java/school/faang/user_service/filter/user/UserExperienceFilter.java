package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserExperienceFilter implements UserFilter {

    @Override
    public Stream<User> applyFilter(Stream<User> users, UserFilterDto userFilterDto) {
        if (userFilterDto.getExperienceMin() != null) {
            users = users.filter(user -> user.getExperience() > userFilterDto.getExperienceMin());
        }

        if (userFilterDto.getExperienceMax() != null) {
            users = users.filter(user -> user.getExperience() < userFilterDto.getExperienceMax());
        }

        return users;
    }
}
