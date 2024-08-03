package school.faang.user_service.filter.user;

import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;

public class UserEmailFilter implements UserFilter {
    @Override
    public boolean checkingForNull(UserFilterDto userFilterDto) {
        return userFilterDto.getEmail() != null;
    }

    @Override
    public boolean filterUsers(User user, UserFilterDto userFilterDto) {
        return user.getEmail().toLowerCase().contains(userFilterDto.getEmail().toLowerCase());
    }
}