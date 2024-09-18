package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.UserFilterStrategy;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFilterService {

    private final List<UserFilterStrategy> userFilterStrategies;

    public List<User> filterUsers(List<User> users, UserFilterDto filter) {
        for (UserFilterStrategy strategy : userFilterStrategies) {
            if (strategy.applyFilter(filter)) {
                users = strategy.filter(users, filter);
            }
        }
        return users;
    }
}
