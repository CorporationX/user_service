package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.profile_picture.ProfilePictureService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final GoalService goalService;
    private final EventService eventService;
    private final UserRepository userRepository;
    private final MentorshipService mentorshipService;
    private final ProfilePictureService profilePictureService;
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${spring.data.redis.channels.user_ban_channel.name}")
    private String userBanChannelName;
    private final ProfileViewEventPublisher profileViewEventPublisher;

    public boolean isUserExist(Long userId) {
        return userRepository.existsById(userId);
    }

    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalStateException("User not found with ID: " + userId));
        profileViewEventPublisher.publishProfileViewEvent(userId);
        return userMapper.toDto(user);
    }

    private boolean shouldGoalBeDeleted(GoalDto goal, Long userId) {
        List<Long> userIds = goal.getUserIds();
        return userIds.size() == 1 && Objects.equals(userIds.get(0), userId);
    }

    private boolean shouldEventBeDeleted(EventDto event, Long userId) {
        // If event owner is not a deactivated user
        if (!Objects.equals(event.getOwnerId(), userId)) {
            return false;
        }

        List<Long> userIds = event.getAttendeesIds();
        return userIds.size() == 1 && Objects.equals(userIds.get(0), userId);
    }

    private void stopUserGoals(Long userId) {
        List<Long> userGoalsForDeleting = new ArrayList<>();
        List<Long> userGoalsForUpdating = new ArrayList<>();

        List<GoalDto> allGoals = goalService.getGoalsByUser(userId);

        for (GoalDto goal : allGoals) {
            if (shouldGoalBeDeleted(goal, userId)) {
                userGoalsForDeleting.add(goal.getId());
            } else {
                userGoalsForUpdating.add(goal.getId());
            }
        }

        goalService.deleteAllByIds(userGoalsForDeleting);
        goalService.removeUserFromGoals(userGoalsForUpdating, userId);
    }

    private void stopUserEvents(Long userId) {
        List<Long> userEventsForDeleting = new ArrayList<>();
        List<Long> userEventsForUpdating = new ArrayList<>();

        List<EventDto> allevents = eventService.getParticipatedEvents(userId);

        for (EventDto event : allevents) {
            if (shouldEventBeDeleted(event, userId)) {
                userEventsForDeleting.add(event.getId());
            } else {
                userEventsForUpdating.add(event.getId());
            }
        }

        eventService.deleteAllByIds(userEventsForDeleting);
        eventService.removeUserFromEvents(userEventsForUpdating, userId);
    }

    private void cancelMentoring(Long userId) {
        mentorshipService.cancelMentoring(userId);
    }


    public void deactivateUser(Long userId) {
        stopUserGoals(userId);
        stopUserEvents(userId);
        cancelMentoring(userId);
    }

    public void createUser(UserDto userDto) throws IOException {
        User user = userMapper.toEntity(userDto);
        profilePictureService.setProfilePicture(user);
        userRepository.save(userMapper.toEntity(userDto));
    }
}
