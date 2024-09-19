package school.faang.user_service.service.user.filter;

import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.Objects;

public class UserFilterByCities implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filterDto) {
        return Objects.nonNull(filterDto.getCities());
    }

    @Override
    public List<User> filterUsers(List<User> users, UserFilterDto filterDto) {
        List<String> cities = filterDto.getCities();
        return users.stream()
                .filter(user -> cities.contains(user.getCity()))
                .toList();
    }
}
