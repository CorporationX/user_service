package school.faang.user_service.service.user.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserPremiumFilter implements UserFilter {


    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getPremium() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filter) {
        return users
                .filter(user -> user.getPremium() != null);

    }

}
