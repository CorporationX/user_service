package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserNotificationDto;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.UserNotificationMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserNotificationMapper userNotificationMapper;

    @Override
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + id));
    }

    @Override
    public UserNotificationDto getUserNotificationById(Long id) {
        return userRepository.findById(id)
                .map(userNotificationMapper::toUserNotificationDto)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + id));
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> ids) {
        return userRepository.findAllById(ids)
                .stream()
                .map(userMapper::toUserDto)
                .toList();
    }


}
