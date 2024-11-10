package school.faang.user_service.filter.userFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class PhonePatternFilter extends UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filters) {
        this.pattern = filters.phonePattern();
        return pattern != null;
    }

    @Override
    public boolean apply(User user) {
        return user.getPhone() != null && user.getPhone().contains(pattern);
    }
}
