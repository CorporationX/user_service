package school.faang.user_service.service.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
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
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public UserDto create(CreateUserDto userDto) {
        createValidation(userDto, minPasswordLength);
        User user = userMapper.toUser(userDto);
        user.setActive(true);
        user.setCountry(countryRepository.getByIdOrThrow(userDto.countryId()));
        user = userRepository.save(user);
        log.info("User {} created", user.getId());
        return userMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public Long delete(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User with id " + userId + " not found!"));
        userRepository.delete(user);
        log.info("User {} deleted", user.getId());
        return user.getId();
    }

    @Transactional
    @Override
    public UserDto update(long userId, UpdateUserDto userDto) {
        long requesterId = userContext.getUserId();
        if (userId != requesterId) {
            throw new ForbiddenException("User " + requesterId + " doesn't match profile owner!");
        }
        User user = userRepository.getByIdOrThrow(userId);
        userMapper.update(userDto, user);
        user.setCountry(countryRepository.getByIdOrThrow(userDto.countryId()));
        user = userRepository.save(user);
        log.info("User {} updated", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateChatIdByEmail(long chatId, String email) { //TODO: изменить параметры на @RequestBody
        User currentUser = userRepository.findByEmailIgnoreCase(email);
        if (currentUser == null) {
            throw new EntityNotFoundException(String.format("User with email '%s' does not exist", email));
        }
        currentUser.setChatId(chatId);
        return userMapper.toUserDto(currentUser);
    }

    @Transactional
    @Override
    public UserDto getUserById(long userId) {
        User user = userRepository.getByIdOrThrow(userId);
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        return userMapper.toUserDtos(users);
    }

    private void createValidation(CreateUserDto userDto, int minPasswordLength) {
        if (userRepository.existsByUsername(userDto.username())) {
            throw new DataValidationException("User with username " + userDto.username() + " already exists!");
        }
        if (userDto.password().length() < minPasswordLength) {
            throw new DataValidationException("Password should be more than " + minPasswordLength + " symbols!");
        }
        if (userRepository.existsByEmailIgnoreCase(userDto.email())) {
            throw new DataValidationException("User with email " + userDto.email() + " already exists!");
        }
        if (userRepository.existsByPhone(userDto.phone())) {
            throw new DataValidationException("User with phone " + userDto.phone() + " already exists!");
        }
    }
}
