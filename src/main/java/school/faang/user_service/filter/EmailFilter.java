package school.faang.user_service.filter;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;

@Validated
public class EmailFilter implements UserFilter{
    @Override
    public boolean apply(UserDto user, @Valid UserFilterDto filter) {
        return filter.getEmailPattern() == null || user.getEmail().matches(filter.getEmailPattern());
    }
}