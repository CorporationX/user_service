package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import static school.faang.user_service.exception.ExceptionMessage.NO_USER_IN_DB;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto getUser(long userId) {
        return userMapper.toDto(userRepository.findById(userId).orElseThrow(()
                -> new DataValidationException(NO_USER_IN_DB.getMessage())));
    }
}
