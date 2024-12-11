package school.faang.user_service.filter.userFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.user.User;

@Component
public class CityPatternFilter extends UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filters) {
        this.pattern = filters.cityPattern();
        return pattern != null;
    }

    @Override
    public boolean apply(User user) {
        return user.getCity() != null && user.getCity().contains(pattern);
    }
}
