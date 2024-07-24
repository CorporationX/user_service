package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class CountryPatternFilter implements UserFilter {
    @Override
    public boolean apply(User user, UserFilterDto filter) {
        return filter.getCountryPattern() == null || user.getCountry().getTitle().contains(filter.getCountryPattern());
    }
}
