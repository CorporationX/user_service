package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.DeactivatedUserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.EventService;
import school.faang.user_service.service.GoalService;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDeactivationService {

    private final UserService userService;
    private final GoalService goalService;
    private final EventService eventService;
    private final MentorshipService mentorshipService;
    private final UserMapper userMapper;

    @Transactional
    public DeactivatedUserDto deactivateUser(long userId) {
        log.info("start to deactivate User by id {}", userId);
        User user = userService.getUserById(userId);

        stopScheduledGoals(user);
        stopScheduledEvents(user);
        removeUserFromParticipatedEvents(user);
        mentorshipService.stopMentorship(user);

        user.setActive(false);

        User savedUser = userService.updateUser(user);

        log.info("User successfully deactivated by id {}", userId);
        return userMapper.toDeactivatedUserDto(savedUser);
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

        List<User> usersToRemoveEvent = new ArrayList<>();
        for (Event event : ownedEvents) {
            List<User> attendees = event.getAttendees();

            attendees.forEach(attendee -> {
                attendee.removeParticipatedEvent(event);
            });
            usersToRemoveEvent.addAll(attendees);
        }
        userService.updateAllUsers(usersToRemoveEvent);
        eventService.deleteAllEvents(ownedEvents);

        user.removeAllOwnedEvents();
        log.info("all scheduled events is stopped and delete");
    }

    private void removeUserFromParticipatedEvents(User user) {
        List<Event> participatedEvents = user.getParticipatedEvents();

        participatedEvents.forEach(participatedEvent -> {
            participatedEvent.removeAttendeeFromEvent(user);
        });
        eventService.updateAllEvents(participatedEvents);

        user.removeAllParticipatedEvents();
        log.info("user {} unsubscribe from participated events", user.getId());
    }
}
