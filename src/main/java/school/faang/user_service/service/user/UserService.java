package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserMapper userMapper;

    @Transactional
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with this id does not exist in the database"));
    }

    public UserDto getUser(long userId) {
        User existedUser = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User with id " + userId + " does not exist"));

        return userMapper.toDto(existedUser);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        ids.forEach(userValidator::validateUserIdIsPositiveAndNotNull);

        return userMapper.toDtos(userRepository.findAllById(ids));
    }
}
