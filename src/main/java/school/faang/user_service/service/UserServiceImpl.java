package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Optional<User> findUserById(long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public UserDto getUserById(long userId) {
        return userMapper.toDto(
                userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("User doesn't exist with id = " + userId))
        );
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        return users.stream().map(userMapper::toDto).toList();
    }

    @Override
    public void banUser(String userIdStr) {
        log.info("Start banning intruder with id: {}", userIdStr);
        Long userId = Long.parseLong(userIdStr);
        userRepository.findById(userId).ifPresent(user -> {
            user.setBanned(true);
            userRepository.save(user);
        });
        log.info("The intruder with id: {} has been banned", userId);
    }
}
