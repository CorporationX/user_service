package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User with id " + userId + " has not found"));
    }

    public UserDto getUserDtoById(long userId) {
        return userMapper.toDto(getUserById(userId));
    }
}