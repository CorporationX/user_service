package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.UserValidator;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final UserRepository userRepository;

    public UserDto getUser(long userId) {
        return userMapper.toDto(userValidator.validateUserExistence(userId));
    }

    public boolean checkUserExistence(long userId) {
        return userRepository.existsById(userId);
    }
}
