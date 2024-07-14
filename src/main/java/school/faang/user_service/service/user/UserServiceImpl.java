package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.user.UserFilterIllegalArgumentException;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.validation.user.filter.UserFilterValidation;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final List<UserFilter> userFilters;
    private final UserFilterValidation userFilterValidation;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        if (userFilterValidation.isNullable(userFilterDto)) {
            throw new UserFilterIllegalArgumentException("UserFilter is nullable");
        }

        List<User> premiumUsers = userRepository.findPremiumUsers().toList();

        if (!userFilterValidation.isAnyFilterApplicable(userFilters, userFilterDto)) {
            return userMapper.toUserDtoList(premiumUsers);
        }

        return userFilters.stream()
                .filter(filter -> filter.isApplicable(userFilterDto))
                .flatMap(filter -> filter.apply(premiumUsers.stream(), userFilterDto))
                .map(userMapper::toUserDto)
                .distinct()
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getRegularUsers(UserFilterDto userFilterDto) {
        if (userFilterValidation.isNullable(userFilterDto)) {
            throw new UserFilterIllegalArgumentException("UserFilter is nullable");
        }

        List<User> regularUsers = userRepository.findRegularUsers().toList();

        if (!userFilterValidation.isAnyFilterApplicable(userFilters, userFilterDto)) {
            return userMapper.toUserDtoList(regularUsers);
        }

        return userFilters.stream()
                .filter(filter -> filter.isApplicable(userFilterDto))
                .flatMap(filter -> filter.apply(regularUsers.stream(), userFilterDto))
                .map(userMapper::toUserDto)
                .distinct()
                .toList();
    }
}
