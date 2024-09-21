package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getUser(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User with id %s not found".formatted(userId));
        }
        UserDto userDto = userMapper.userToUserDto(user.get());
        log.debug("User with id %s found".formatted(userId));
        return userDto;
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> userIds) {
        log.debug("Trying to find users with ids %s"
                .formatted(String.join(", ", userIds.stream().map(Object::toString).toList())));
        List<User> users = userRepository.findAllById(userIds);
        if (users.isEmpty()) {
            throw new EntityNotFoundException("There are no users with such ids");
        }
        List<Long> foundUserIds = users.stream().map(User::getId).toList();
        log.debug("Users with ids %s found"
                .formatted(String.join(", ", foundUserIds.stream().map(Object::toString).toList())));
        List<UserDto> userDtos = userMapper.usersToUserDtos(users);
        return userDtos;
    }
}