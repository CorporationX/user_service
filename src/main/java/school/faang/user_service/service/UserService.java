package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentor.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto getUser(long id) {
        User foundUser = userRepository.findById(id).orElseThrow(() -> {
            throw new IllegalArgumentException("User not found");
        });

        return userMapper.toUserDto(foundUser);
    }

    public List<UserDto> getUsersByIds(List<Long> usersIds) {
        List<User> users = userRepository.findAllById(usersIds);
        return userMapper.toUserListDto(users);
    }
}
