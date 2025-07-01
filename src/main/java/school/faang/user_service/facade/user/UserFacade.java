package school.faang.user_service.facade.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterRequestDto;
import school.faang.user_service.dto.user.UserNotificationResponseDto;
import school.faang.user_service.dto.user.UserRegisterRequestDto;
import school.faang.user_service.dto.user.UserRegisterResponseDto;
import school.faang.user_service.dto.user.UserResponseDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.user.UserEntityMapper;
import school.faang.user_service.mapper.user.UserFilterMapper;
import school.faang.user_service.model.user.UserFilter;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserFacade {
    private final UserEntityMapper userEntityMapper;
    private final UserService userService;
    private final UserFilterMapper userFilterMapper;

    public UserRegisterResponseDto registrationUser(UserRegisterRequestDto userRegisterRequestDto) {
        User user = userEntityMapper.toUserEntity(userRegisterRequestDto);
        log.debug("Mapping UserRegisterRequestDto to User entity. DTO content: {}. Entity content: {}.",
                userRegisterRequestDto, user);

        user = userService.registrationUser(user, userRegisterRequestDto.getCountryId());

        userService.createAvatarUser(user.getId());

        UserRegisterResponseDto userRegisterResponseDto = userEntityMapper.toUserRegisterResponseDto(user);
        log.debug("Mapping User entity to UserRegisterResponseDto. Entity content: {}. DTO content: {}.",
                user, userRegisterResponseDto);

        return userRegisterResponseDto;
    }

    public UserResponseDto getCurrentUser() {
        User user = userService.getCurrentUser();

        UserResponseDto userResponseDto = userEntityMapper.toUserLiteResponseDto(user);
        log.info("Mapping User entity to UserResponseDto. Entity content: {}. DTO content: {}.",
                user, userResponseDto);
        return userResponseDto;
    }

    public UserResponseDto getUserByIdOrThrow(long userId) {
        User user = userService.getUserByIdOrThrow(userId);

        UserResponseDto userResponseDto = userEntityMapper.toUserLiteResponseDto(user);
        log.info("Mapping User entity to UserResponseDto. Entity content: {}. DTO content: {}.",
                user, userResponseDto);
        return userResponseDto;
    }

    public UserNotificationResponseDto getNotificationUserById(long userId) {
        User user = userService.getUserByIdOrThrow(userId);

        UserNotificationResponseDto userResponseDto = userEntityMapper.toUserNotificationResponseDto(user);
        log.debug("Mapping User entity to UserNotificationResponseDto. Entity content: {}. DTO content: {}.",
                user, userResponseDto);
        return userResponseDto;
    }

    public List<UserResponseDto> getUsersByIds(List<Long> userIds) {
        List<User> users = userService.getUsersByIds(userIds);

        List<UserResponseDto> userResponseDtoList = userEntityMapper.toUserResponseDtoList(users);
        log.info("Mapping User entity list to UserResponseDto list. Entity content: {}. DTO content: {}.",
                users, userResponseDtoList);
        return userResponseDtoList;
    }

    public List<UserNotificationResponseDto> getNotificationUserByIds(List<Long> userIds) {
        List<User> users = userService.getUsersByIds(userIds);

        List<UserNotificationResponseDto> userResponseDtoList =
                userEntityMapper.toUserNotificationResponseDtoList(users);
        log.debug("Mapping User entity list to UserNotificationResponseDto list. Entity content: {}. DTO content: {}.",
                users, userResponseDtoList);
        return userResponseDtoList;
    }

    public List<UserResponseDto> filter(UserFilterRequestDto filterDto) {
        UserFilter filter = userFilterMapper.toFilter(filterDto);
        log.debug("Mapping UserFilterRequestDto to UserFilter. DTO content: {}. Model content: {}.",
                filterDto, filter);

        List<User> users = userService.getUsersByFilter(filter);

        List<UserResponseDto> userResponseDtoList = userEntityMapper.toUserLiteResponseDtoList(users);
        log.debug("Mapping User list to UserLiteResponseDto list. Entity content: {}. DTO content: {}.",
                users, userResponseDtoList);

        return userResponseDtoList;
    }
}
