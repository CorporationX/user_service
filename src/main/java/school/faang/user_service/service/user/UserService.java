package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.filter.UserFilter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final List<UserFilter> userFilters;

    @Transactional(readOnly = true)
    public List<User> findPremiumUser(UserFilterDto filterDto) {
        List<User> premiumUsers = userRepository.findPremiumUsers().toList();
        return userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(filterDto))
                .reduce(premiumUsers, (list, filter) -> filter.filterUsers(list, filterDto), (list, filter) -> list);
    }
}
