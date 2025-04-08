package school.faang.user_service.service;

import school.faang.user_service.dto.UserFilterRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.UserFilter;

import java.util.stream.Stream;

public class UserMinExperienceFilterTest implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterRequestDto userFilterRequest) {
        return true;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterRequestDto userFilterRequest) {
        return users.filter(user -> user.getExperience() >= userFilterRequest.experienceMin());
    }
}
