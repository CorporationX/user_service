package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {

    // TODO: !!! в таске сказано "используя метод findPremiumUsers из PremiumRepository"
    //       но метод findPremiumUsers есть в UserRepository, а в PremiumRepository такого нет
    //       думаю описка в таске
    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final List<UserFilter> userFilters;

    @Transactional
    public List<UserDto> getPremiumUsers(UserFilterDto filterDto) {
        Stream<User> premiumUsers = userRepository.findPremiumUsers();
        return userFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(premiumUsers,
                        (stream, filter) -> filter.apply(stream, filterDto),
                        (s1, s2) -> s1)
                .map(mapper::toDto)
                .toList();
    }
}
