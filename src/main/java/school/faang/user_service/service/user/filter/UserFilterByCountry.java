package school.faang.user_service.service.user.filter;

import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.Objects;

public class UserFilterByCountry implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filterDto) {
        return Objects.nonNull(filterDto.getCountry());
    }

    @Override
    public List<User> filterUsers(List<User> users, UserFilterDto filterDto) {
        List<String> country = filterDto.getCountry();
        return users.stream().filter(user -> country.contains(user.getCountry().getTitle())).toList();
    }
}
