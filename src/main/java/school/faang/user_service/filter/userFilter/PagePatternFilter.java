package school.faang.user_service.filter.userFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.model.User;

@Component
public class PagePatternFilter extends UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filters) {
        int pattern = filters.page();
        return pattern > 0;
    }

    @Override
    public boolean apply(User user) {
        return true;
    }
}
