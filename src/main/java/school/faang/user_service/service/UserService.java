package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.UserValidator;

import java.util.List;

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

    public List<UserDto> getUserFollowers(long userId) {
        User user = userValidator.validateUserExistence(userId);
        return user.getFollowers().stream().map(userMapper::toDto).toList();
    }

    public boolean checkAllFollowersExist(List<Long> followerIds) {
        return userValidator.checkAllFollowersExist(followerIds);
    }
}
