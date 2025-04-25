package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static school.faang.user_service.messages.ErrorMessages.USER_NOT_FOUND_ERROR;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public boolean isExists(long userId) {
        return userRepository.existsById(userId);
    }

    @Transactional
    public void banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND_ERROR, userId)));
        user.setBanned(true);
        userRepository.save(user);
    }

    public UserDto getUser(long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            String message = USER_NOT_FOUND_ERROR.formatted(userId);
            log.error(message);
            throw new UserNotFoundException(message);
        }
        return userMapper.toDto(userOptional.get());
    }
}

