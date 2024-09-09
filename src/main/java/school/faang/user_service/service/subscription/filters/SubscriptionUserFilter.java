package school.faang.user_service.service.subscription.filters;

import lombok.NonNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

@Component
public class SubscriptionUserFilter {
    private List<UserFilter> userFilters;

    public List<User> filterUsers(@NonNull Stream<User> users, @NonNull UserFilterDto filters) {
        return userFilters.stream()
                .reduce(users,
                        ((userStream, userFilter) -> userFilter.apply(userStream, filters)),
                        ((userStream, newUserStream) -> newUserStream))
                .toList();
    }
}
