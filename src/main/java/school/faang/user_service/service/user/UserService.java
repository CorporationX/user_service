package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.exceptions.UserNotFoundException;
import school.faang.user_service.service.exceptions.messageerror.MessageError;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto getUser(long userId) {
        return userMapper.toDto(getUserEntityById(userId));
    }

    public List<UserDto> getUsersByIds(List<Long> userIds) {
        return userMapper.toDto(getUsersEntityByIds(userIds));
    }

    public User getUserEntityById(long userId){
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(MessageError.USER_NOT_FOUND_EXCEPTION));
    }

    public List<User> getUsersEntityByIds(List<Long> userIds){
        return userRepository.findAllById(userIds);
    }
}
