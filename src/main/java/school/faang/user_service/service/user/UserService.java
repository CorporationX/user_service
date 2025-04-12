package school.faang.user_service.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserBanDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static school.faang.user_service.messages.ErrorMessages.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public boolean isExists(long userId) {
        return userRepository.existsById(userId);
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

    @Transactional
    public void banUser(UserBanDto userBanDto) {
        Optional<User> userOptional = userRepository.findById(userBanDto.getUserId());
        if (userOptional.isEmpty()) {
            String message = USER_NOT_FOUND_ERROR.formatted(userBanDto.getUserId());
            log.error(message);
            throw new UserNotFoundException(message);
        }
        User user = userOptional.get();
        user.setBanned(true);
    }
}
