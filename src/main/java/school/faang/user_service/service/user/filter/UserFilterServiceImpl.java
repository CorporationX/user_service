package school.faang.user_service.service.user.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserFilterServiceImpl implements UserFilterService {

    private final List<UserFilter> userFilters;

    @Override
    public Stream<User> applyFilters(Stream<User> users, UserFilterDto userFilterDto) {
        if (userFilterDto != null) {
            for (UserFilter userFilter : userFilters) {
                users = userFilter.applyFilter(users, userFilterDto);
            }
        }

        return users;
    }
}
