package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.subscription.UserDto;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;
    @Value("${dicebear.url}")
    private String dicebearUrl;

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
        //TODO Нужно реализовать логику создания юзера
        User user = userRepository.findById(userDto.getId()).orElseThrow(() -> new UserNotFoundException("User is not found"));
        setDefaultAvatar(user);
        return userDto;
    }

    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        Stream<User> premiumUsers = userRepository.findPremiumUsers();

        premiumUsers = filter(userFilterDto, premiumUsers);
        return userMapper.toUserListDto(premiumUsers.toList());
    }

    @Transactional
    public void banUser(long id) {
        userRepository.banUser(id);
    }

    private Stream<User> filter(UserFilterDto userFilterDto, Stream<User> premiumUsers) {
        for (UserFilter filter : userFilters) {
            if (filter.isApplicable(userFilterDto)) {
                premiumUsers = filter.apply(premiumUsers, userFilterDto);
            }
        }
        return premiumUsers;
    }

    private void setDefaultAvatar(User user) {
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(dicebearUrl + user.getUsername() + "&scale=" + 130);
        userProfilePic.setSmallFileId(dicebearUrl + user.getUsername() + "&scale=" + 22);
        user.setUserProfilePic(userProfilePic);
    }
}
