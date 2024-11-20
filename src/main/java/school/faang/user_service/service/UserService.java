package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.DeactivatedUserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.exceptions.ResourceNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final GoalService goalService;
    private final MentorshipService mentorshipService;
    private final UserMapper userMapper;

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public List<User> getAllUsersByIds(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    @Transactional
    public DeactivatedUserDto deactivateUser(long userId) {
        log.info("start to deactivate User by id {}", userId);
        User user = getUserById(userId);

        stopScheduledGoals(user);
        stopScheduledEvents(user);
        removeUserFromParticipatedEvents(user);
        mentorshipService.stopMentorship(user);

        user.setActive(false);

        User savedUser = userRepository.save(user);

        log.info("User successfully deactivated by id {}", userId);
        return userMapper.toDeactivatedUserDto(savedUser);
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new
                EntityNotFoundException("User do not found by " + userId));
    }

    private void stopScheduledGoals(User user) {
        List<Goal> goals = user.getGoals();
        List<Goal> settingGoals = user.getSettingGoals();

        goals.forEach(goal -> goal.removeExecutingUser(user));

        goalService.removeGoalsWithoutExecutingUsers(goals);
        goalService.removeGoalsWithoutExecutingUsers(settingGoals);

        user.removeAllGoals();
        log.info("all scheduled goals is stopped: \t goals {} \t settingGoals{}"
                , user.getGoals(), user.getSettingGoals());
    }

    private void stopScheduledEvents(User user) {
        List<Event> ownedEvents = user.getOwnedEvents();

        for (Event event : ownedEvents) {
            List<User> attendees = event.getAttendees();

            attendees.forEach(attendee -> {
                attendee.removeParticipatedEvent(event);
                userRepository.save(attendee);
            });

            eventRepository.deleteById(event.getId());
        }

        user.removeAllOwnedEvents();
        log.info("all scheduled events is stopped and delete");
    }

    private void removeUserFromParticipatedEvents(User user) {
        List<Event> participatedEvents = user.getParticipatedEvents();

        participatedEvents.forEach(participatedEvent -> {
            participatedEvent.removeAttendeeFromEvent(user);
            eventRepository.save(participatedEvent);
        });

        user.removeAllParticipatedEvents();
        log.info("user {} unsubscribe from participated events", user.getId());
    }


    public void saveUser(User user) {
        userRepository.save(user);
    }
}
