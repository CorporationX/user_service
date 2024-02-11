package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.exception.mentorship.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final List<UserFilter> userFilters;


    public User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(()
                -> new DataValidationException("User was not found"));
    }

    public List<User> getPremiumUsers(UserFilterDto userFilterDto) {
        Stream<User> premiumUsers = userRepository.findPremiumUsers();
        userFilters.stream()
                .filter(filter -> filter.isApplicable(userFilterDto))
                .forEach(filter -> filter.apply(premiumUsers, userFilterDto));
        return premiumUsers.toList();
    }
}
