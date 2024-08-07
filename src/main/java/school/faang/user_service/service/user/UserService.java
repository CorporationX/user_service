package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.validator.user.UserFilterValidation;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final List<UserFilter> userFilters;
    private final UserFilterValidation userFilterValidation;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        if (userFilterValidation.isNullable(userFilterDto)) {
            throw new DataValidationException("UserFilter is nullable");
        }

        List<User> premiumUsers = userRepository.findPremiumUsers().toList();

        if (!userFilterValidation.isAnyFilterApplicable(userFilters, userFilterDto)) {
            return userMapper.toDtoList(premiumUsers);
        }

        return filterUsers(userFilterDto, premiumUsers);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getRegularUsers(UserFilterDto userFilterDto) {
        if (userFilterValidation.isNullable(userFilterDto)) {
            throw new DataValidationException("UserFilter is nullable");
        }

        List<User> regularUsers = userRepository.findRegularUsers().toList();

        if (!userFilterValidation.isAnyFilterApplicable(userFilters, userFilterDto)) {
            return userMapper.toDtoList(regularUsers);
        }

        return filterUsers(userFilterDto, regularUsers);
    }

    private List<UserDto> filterUsers(UserFilterDto userFilterDto, List<User> users) {
        return userFilters.stream()
                .filter(filter -> filter.isApplicable(userFilterDto))
                .reduce(users.stream(),
                        (stream, filter) -> filter.apply(stream, userFilterDto),
                        Stream::concat)
                .map(userMapper::toDto)
                .toList();

    }

    @Transactional
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }
}