package school.faang.user_service.filter.impl;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.UserFilter;

import java.util.stream.Stream;

@Component
public class UserMinExperienceFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterRequestDto userFilterRequestDto) {
        return userFilterRequestDto.experienceMin() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterRequestDto userFilterRequestDto) {
        return users.filter(user -> user.getExperience() >= userFilterRequestDto.experienceMin());
    }
}
