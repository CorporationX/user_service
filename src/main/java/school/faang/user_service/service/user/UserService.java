package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto getById(Long id) {
        User foundedUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with %s id doesn't exist", id)));

        return userMapper.toDto(foundedUser);
    }
}
