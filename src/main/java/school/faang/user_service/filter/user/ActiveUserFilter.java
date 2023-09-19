package school.faang.user_service.filter.user;

import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class ActiveUserFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto userFilterDto) {
        return userFilterDto.isActive();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto userFilterDto) {
        if (userFilterDto.isActive()){
            return users.filter(User::isActive);
        }

        return  users.filter(user -> !user.isActive());
    }
}
