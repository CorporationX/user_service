package school.faang.user_service.service.subscription.filters;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class UserFiltersApplier {
    private final List<UserFilter> userFilters;

    public List<User> applyFilters(@NonNull Stream<User> users, @NonNull UserFilterDto filters) {
        return userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(users,
                        ((userStream, userFilter) -> userFilter.apply(userStream, filters)),
                        ((userStream, newUserStream) -> newUserStream))
                .toList();
    }
}
