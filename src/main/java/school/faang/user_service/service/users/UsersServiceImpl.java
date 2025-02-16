package school.faang.user_service.service.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.events.UserProfileViewEvent;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.UserProfileViewEventPublisher;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserProfileViewEventPublisher userProfileViewEventPublisher;
    private final UserContext userContext;

    @Override
    public User findByIdOrThrow(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User is not exists! id: " + userId));
    }

    @Override
    public UserDto getUser(Long userId) {
        User user = findByIdOrThrow(userId);
        UserDto userDto = userMapper.toUserDto(user);
        Long visitorId = userContext.getUserId();
        userProfileViewEventPublisher.publish(new UserProfileViewEvent(userId, visitorId, LocalDateTime.now()));
        log.info("Profile of user {} was viewed", userDto.getUsername());
        return userDto;
    }
}
