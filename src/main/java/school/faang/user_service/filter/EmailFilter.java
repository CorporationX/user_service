package school.faang.user_service.filter;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;

public class EmailFilter implements UserFilter{
    @Override
    public boolean apply(UserDto user, UserFilterDto filter) {
        return filter.getEmailPattern() == null || user.getEmail().matches(filter.getEmailPattern());
    }
}