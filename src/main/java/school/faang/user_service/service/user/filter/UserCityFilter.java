package school.faang.user_service.service.user.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserExtendedFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.UserFilter;

import java.util.function.Predicate;

@Component
public class UserCityFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserExtendedFilterDto filters) {
        return filters.getCityPattern() != null;
    }

    @Override
    public Predicate<User> getPredicate(UserExtendedFilterDto filters) {
        return user -> user.getCity() != null && user.getCity().contains(filters.getCityPattern());
    }
}
