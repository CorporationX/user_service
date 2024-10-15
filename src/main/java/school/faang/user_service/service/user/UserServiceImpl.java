package school.faang.user_service.service.user;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.user.ProfileViewEventDto;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.publisher.ProfileViewEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final MentorshipService mentorshipService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final GoalRepository goalRepository;
    private final UserMapper mapper;
    private final UserContext userContext;
    private final ProfileViewEventPublisher profileViewEventPublisher;

    @Transactional
    public void deactivateUser(@NonNull Long id) {
        log.info("deactivating user with id: {}", id);
        goalRepository.deleteUnusedGoalsByMentorId(id);
        eventRepository.deleteAllByOwnerId(id);
        mentorshipService.stopMentorship(id);
        userRepository.updateUserActive(id, false);
        log.info("deactivated user with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUser(long id) {
        publishProfileViewEvent(id);

        return userRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> ids) {
        return userRepository.findAllById(ids).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    private void publishProfileViewEvent(long receiverId) {
        ProfileViewEventDto profileViewEventDto = ProfileViewEventDto.builder()
                .receiverId(receiverId)
                .actorId(userContext.getUserId())
                .receivedAt(LocalDateTime.now())
                .build();
        profileViewEventPublisher.publish(profileViewEventDto);
        log.info("Event sent: profileViewEvent");
    }
}
