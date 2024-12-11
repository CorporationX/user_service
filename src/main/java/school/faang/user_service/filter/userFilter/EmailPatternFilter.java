package school.faang.user_service.filter.userFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.user.User;

@Component
public class EmailPatternFilter extends UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filters) {
        this.pattern = filters.aboutPattern();
        return pattern != null;
    }

    @Override
    public boolean apply(User user) {
        return user.getEmail() != null && user.getEmail().contains(pattern);
    }
}
