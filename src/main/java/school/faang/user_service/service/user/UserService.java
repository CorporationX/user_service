package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.filter.UserFilter;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final List<UserFilter> userFilters;

    @Transactional
    public List<User> findPremiumUser(UserFilterDto filterDto) {
        List<User> premiumUsers = userRepository.findPremiumUsers().toList();

        List<UserFilter> applicableFilters = userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(filterDto))
                .toList();

        return premiumUsers.stream()
                .filter(user -> userMatchesFilters(user, applicableFilters, filterDto))
                .toList();
    }

    private boolean userMatchesFilters(User user, List<UserFilter> filters, UserFilterDto filterDto) {
        return filters.stream()
                .allMatch(filter -> filter.test(user, filterDto));
    }
}
