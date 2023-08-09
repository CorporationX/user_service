package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.dto.subscription.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    public UserDto getUser(long id) {
        User foundUser = userRepository.findById(id).orElseThrow(() -> {
            throw new UserNotFoundException("User with id " + id + " not found");
        });
        log.info("Return user with id: {}", foundUser.getId());
        return userMapper.toUserDto(foundUser);
    }

    public List<UserDto> getUsersByIds(List<Long> usersIds) {
        List<User> users = userRepository.findAllById(usersIds);
        log.info("Return list of users: {}", users);
        return userMapper.toUserListDto(users);
    }

    public UserDto signup(UserDto userDto) {
        //TODO Вместо 40 строки должна быть логика создания юзера
        User user = userRepository.findById(userDto.getId()).orElseThrow(() -> new UserNotFoundException("User is not found"));
        setDefaultAvatar(user);
        return userDto;
    }

    private void setDefaultAvatar(User user) {
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId("https://api.dicebear.com/6.x/adventurer/svg?seed=" + user.getUsername() + "&scale=" + 130);
        userProfilePic.setSmallFileId("https://api.dicebear.com/6.x/adventurer/svg?seed=" + user.getUsername() + "&scale=" + 22);
        user.setUserProfilePic(userProfilePic);
    }
}
