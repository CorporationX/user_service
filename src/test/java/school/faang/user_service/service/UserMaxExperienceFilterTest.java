package school.faang.user_service.service;

import school.faang.user_service.dto.UserFilterRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.UserFilter;

import java.util.stream.Stream;

public class UserMaxExperienceFilterTest implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterRequest userFilterRequest) {
        return true;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterRequest userFilterRequest) {
        return users.filter(user -> user.getExperience() <= userFilterRequest.experienceMax());
    }
}
