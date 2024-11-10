package school.faang.user_service.filter.userFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class CountryPatternFilter extends UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filters) {
        this.pattern = filters.countryPattern();
        return pattern != null;
    }

    @Override
    public boolean apply(User user) {
        return user.getCountry() != null && user.getCountry().getTitle().contains(pattern);
    }
}
