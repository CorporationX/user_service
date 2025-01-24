package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class UserExperienceMaxFilter extends UserFilter {

    @Override
    public Object getFilterFieldValue(UserFilterDto filters) {
        return filters.getExperienceMax();
    }

    @Override
    public boolean apply(User user, UserFilterDto filters) {
        return Objects.requireNonNullElse(user.getExperience(), 0) <= filters.getExperienceMax();
    }
}
