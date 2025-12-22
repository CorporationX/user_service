package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.dto.user.UserUpdateDto;
import school.faang.user_service.dto.user.UserViewDto;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${user.password.min.length}")
    private int minPasswordLength;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final UserMapper userMapper;
    private final UserContext userContext;

    @Override
    @Transactional
    public UserViewDto create(UserCreateDto userDto) {
        validateUserPassword(userDto);
        User user = userMapper.toUser(userDto);

        Country country = countryRepository.getByIdOrThrow(userDto.countryId());
        user.setCountry(country);

        user = userRepository.save(user);
        log.info("Пользователь id={} создан", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserViewDto update(Long userId, UserUpdateDto userDto) {
        Long requesterId = userContext.getUserId();
        validateUserProfileOwner(userId, requesterId);

        User user = userRepository.getByIdOrThrow(userId);
        userMapper.update(userDto, user);
        Country country = countryRepository.getByIdOrThrow(userDto.countryId());
        user.setCountry(country);

        user = userRepository.save(user);
        log.info("Пользователь id={} обновлен", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    public UserViewDto getById(Long userId) {
        User user = userRepository.getByIdOrThrow(userId);
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserViewDto> getByIds(List<Long> userIds) {
        List<User> user = userRepository.findAllById(userIds);
        return user.stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    public List<Long> getIdsActiveUsers(int limit) {
        return userRepository.findAllUser(limit);
    }

    public List<Long> getAllUsers(Integer limit) {
        return userRepository.findAllUser(limit);
    }

    private void validateUserPassword(UserCreateDto userDto) {
        if (userDto.password().length() < minPasswordLength) {
            throw new DataValidationException("Пароль не может быть более чем " + minPasswordLength + " символов!");
        }
    }

    private void validateUserProfileOwner(Long userId, Long requesterId) {
        if (!userId.equals(requesterId)) {
            throw new ForbiddenException("Пользователь " + requesterId + " не соответствует владельцу профиля!");
        }
    }
}
