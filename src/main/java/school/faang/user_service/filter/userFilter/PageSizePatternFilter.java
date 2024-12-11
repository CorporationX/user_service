package school.faang.user_service.filter.userFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.user.User;

@Component
public class PageSizePatternFilter extends UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filters) {
        int pattern = filters.pageSize();
        return pattern > 0;
    }

    @Override
    public boolean apply(User user) {
        return true;
    }
}
