package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.user.filter.UserFilter;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PremiumRepository premiumRepository;
    private final List<UserFilter> userFilters;

    @Transactional(readOnly = true)
    public List<User> getPremiumUsers(UserFilterDto userFilterDto) {
        Stream<Premium> premiums = premiumRepository.findPremiumUsers();
        Stream<User> users = premiums.map(Premium::getUser);
        return filterUsers(users, userFilterDto);
    }

    public List<User> filterUsers(Stream<User> users, UserFilterDto userFilterDto) {
        return userFilters
            .stream()
            .filter(f -> f.isApplicable(userFilterDto))
            .reduce(users,
                (stream, filter) -> filter.apply(stream, userFilterDto),
                (s1, s2) -> s1)
            .toList();
    }
}
