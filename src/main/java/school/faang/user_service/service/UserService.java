package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AvatarService avatarService;

    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        avatarService.setDefaultUserAvatar(user);
        return userMapper.toDto(userRepository.save(user));
    }
}
