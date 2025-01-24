package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class UserPhoneFilter extends UserFilter {

    @Override
    public Object getFilterFieldValue(UserFilterDto filters) {
        return filters.getPhonePattern();
    }

    @Override
    public boolean apply(User user, UserFilterDto filters) {
        return Objects.requireNonNullElse(user.getPhone(), "")
                .contains(filters.getPhonePattern());
    }
}
