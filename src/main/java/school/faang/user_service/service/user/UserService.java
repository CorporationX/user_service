package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.ProfileViewEventDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publishers.ProfileViewEventPublisher;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserContext userContext;
    private final ProfileViewEventPublisher profileViewEventPublisher;
    private final EventMapper eventMapper;

    public UserDto getUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found!"));
        sendProfileViewEventToPublisher(userId);
        return userMapper.toDto(user);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        return userMapper.toDto(userRepository.findAllById(ids));
    }

    private void sendProfileViewEventToPublisher(long userId){
        ProfileViewEventDto event = ProfileViewEventDto.builder()
                .observerId(userContext.getUserId())
                .observedId(userId)
                .viewedAt(LocalDateTime.now())
                .build();
        profileViewEventPublisher.publish(event);
        log.info("Successfully sent data to analytics-service");
    }
}
