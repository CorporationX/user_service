package school.faang.user_service.filter.userFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.user.User;

@Component
public class NamePatternFilter extends UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filters) {
        this.pattern = filters.namePattern();
        return pattern != null;
    }

    @Override
    public boolean apply(User user) {
        return user.getUsername() != null && user.getUsername().contains(pattern);
    }
}