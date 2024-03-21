package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto getUser(@PathVariable long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException());
        return userMapper.toDto(user);
    }

    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        List users = userRepository.findAllById(ids);
        return userMapper.toDto(users);
    }
}
