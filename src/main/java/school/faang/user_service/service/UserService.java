package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.UserValidator;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserMapper userMapper;

    public UserDto getUser(long userId) {
        userValidator.validateUserId(userId);
        User user = userRepository.findById(userId).get();

        return userMapper.toDto(user);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        ids.forEach(userValidator::validateUserId);
        Stream<User> userStream = StreamSupport.stream(userRepository.findAllById(ids).spliterator(), false);

        return userStream.map(userMapper::toDto).toList();
    }
}
