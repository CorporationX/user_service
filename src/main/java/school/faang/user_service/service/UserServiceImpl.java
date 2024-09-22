package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PremiumRepository premiumRepository;
    private final List<UserFilter> filters;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {

        Iterable<Premium> premiumIterable = premiumRepository.findAll();

        List<Premium> premiums = StreamSupport.stream(premiumIterable.spliterator(), false)
                .toList();

        return premiums.stream()
                .map(premium -> userMapper.toDto(premium.getUser()))
                .filter(userDto -> filters.stream()
                        .allMatch(filter -> filter.apply(userDto, userFilterDto)))
                .toList();
    }

    private Stream<User> findPremiumUsers(List<UserDto> users, UserFilterDto filter) {
        return users.stream()
                .filter(user -> filters.stream().allMatch(f -> f.apply(user, filter)))
                .map(userMapper::toEntity);
    }
}
