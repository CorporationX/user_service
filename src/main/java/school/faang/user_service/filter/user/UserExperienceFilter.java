package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;

import java.util.stream.Stream;

@Component
public class UserExperienceFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFiltersDto userFiltersDto) {
        return userFiltersDto.experienceMin() != 0 || userFiltersDto.experienceMax() != 0;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFiltersDto userFiltersDto) {
        return users
                .filter(user ->
                        user.getExperience() >= userFiltersDto.experienceMin()
                                && user.getExperience() <= userFiltersDto.experienceMax());
    }
}
