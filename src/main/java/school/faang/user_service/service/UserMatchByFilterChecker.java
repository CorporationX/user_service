package school.faang.user_service.service;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.ArrayList;
import java.util.List;

public class UserMatchByFilterChecker {
    private final List<UserFilterStrategy> filters;

    public UserMatchByFilterChecker() {
        this.filters = new ArrayList<>();
        this.filters.add(new UsernamePatternFilter());
        this.filters.add(new AboutPatternFilter());
        this.filters.add(new EmailPatternFilter());
        this.filters.add(new CityPatternFilter());
        this.filters.add(new PhonePatternFilter());
        this.filters.add(new ExperienceMinPatternFilter());
        this.filters.add(new ExperienceMaxPatternFilter());
    }

    boolean isUserMatchFiltration(User user, UserFilterDto filter) {
        for (UserFilterStrategy filterStrategy : filters) {
            if (!filterStrategy.check(user, filter)) {
                return false;
            }
        }
        return true;
    }
}
