package school.faang.user_service.filter.user;

import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;

public class UserCityFilter implements UserFilter {
    @Override
    public boolean checkingForNull(UserFilterDto userFilterDto) {
        return userFilterDto.getCity() != null;
    }

    @Override
    public boolean filterUsers(User user, UserFilterDto userFilterDto) {
        return user.getCity().toLowerCase().contains(userFilterDto.getCity().toLowerCase());
    }
}