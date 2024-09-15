package school.faang.user_service.validator.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.filter.UserFilter;

import java.util.List;

@Component
public class UserFilterValidation {

    public boolean isNullable(UserFilterDto userFilterDto) {
        return userFilterDto == null;
    }

    public boolean isAnyFilterApplicable(List<UserFilter> filters, UserFilterDto userFilterDto) {
        return filters.stream()
                .anyMatch(filter -> filter.isApplicable(userFilterDto));
    }
}
