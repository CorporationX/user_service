package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataGettingException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import static school.faang.user_service.exception.ExceptionMessage.NO_SUCH_USER_EXCEPTION;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto getUserById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataGettingException(NO_SUCH_USER_EXCEPTION.getMessage()));

        return userMapper.toDto(user);
    }

    public boolean existsById(long userId) {
        return userRepository.existsById(userId);
    }
}
