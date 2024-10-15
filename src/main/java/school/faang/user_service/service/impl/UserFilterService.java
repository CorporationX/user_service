package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.model.filter_dto.UserFilterDto;
import school.faang.user_service.model.entity.User;
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
