package school.faang.user_service.filter.impl;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.UserFilter;

import java.util.stream.Stream;

@Component
public class UserMaxExperienceFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterRequest userFilterRequest) {
        return userFilterRequest.experienceMax() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterRequest userFilterRequest) {
        return users.filter(user -> user.getExperience() <= userFilterRequest.experienceMax());
    }
}
