package school.faang.user_service.filter.userFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class AboutPatternFilter extends UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filters) {
        this.pattern = filters.aboutPattern();
        return pattern != null;
    }

    @Override
    public boolean apply(User user) {
        return pattern != null && user.getAboutMe().contains(pattern);
    }
}
