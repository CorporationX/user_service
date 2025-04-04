package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.event.EventDTO;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final GoalMapper goalMapper;
    private final GoalService goalService;
    private final EventService eventService;
    private final MentorshipService mentorshipService;

    private static final int MONTHS = 3;

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public UserDto findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return userMapper.toDto(user);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);

        if (users.isEmpty()) {
            return Collections.emptyList();
        } else {
            return users.stream()
                    .map(userMapper::toDto)
                    .toList();
        }
    }

    public UserDto activateUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(id)
        );

        if (user.getUpdatedAt().isAfter(LocalDateTime.now().minusMonths(MONTHS))) {
            user.setActive(true);
        }

        return userMapper.toDto(userRepository.save(user));
    }

    public UserDto deactivateUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(id)
        );

        stopUserGoals(id);
        stopUserEvents(id);

        user.setActive(false);

        deleteMentorship(id);

        return userMapper.toDto(userRepository.save(user));
    }

    private void stopUserGoals(Long userId) {
        List<Long> userGoalsForDeleting = new ArrayList<>();
        List<Long> userGoalsForUpdating = new ArrayList<>();

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        List<GoalDto> allGoals = user.getGoals().stream().map(goalMapper::goalToGoalDto).toList();

        for (GoalDto goal : allGoals) {
            if (shouldGoalBeDeleted(goal, userId)) {
                userGoalsForDeleting.add(goal.id());
            } else {
                userGoalsForUpdating.add(goal.id());
            }
        }

        goalService.deleteAllByIds(userGoalsForDeleting);
        goalService.removeUserFromGoals(userGoalsForUpdating, userId);
    }

    private void stopUserEvents(Long userId) {
        List<Long> userEventsForDeleting = new ArrayList<>();
        List<Long> userEventsForUpdating = new ArrayList<>();

        List<EventDTO> allEvents = eventService.getParticipatedEvents(userId);

        for (EventDTO event : allEvents) {
            if (shouldEventBeDeleted(event, userId)) {
                userEventsForDeleting.add(event.getId());
            } else {
                userEventsForUpdating.add(event.getId());
            }
        }

        eventService.deleteAllByIds(userEventsForDeleting);
        eventService.removeUserFromEvents(userEventsForUpdating, userId);
    }

    private void deleteMentorship(Long userId) {
        mentorshipService.deleteMentorship(userId);
    }

    private boolean shouldGoalBeDeleted(GoalDto goal, Long userId) {
        List<Long> userIds = goal.userIds();
        return userIds.size() == 1 && Objects.equals(userIds.get(0), userId);
    }

    private boolean shouldEventBeDeleted(EventDTO event, Long userId) {
        if (!Objects.equals(event.getOwnerId(), userId)) {
            return false;
        }

        List<Long> userIds = event.getAttendeesIds();
        return userIds.size() == 1 && Objects.equals(userIds.get(0), userId);
    }
}
