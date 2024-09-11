package school.faang.user_service.service.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.user.UserDeactivatedException;
import school.faang.user_service.exception.user.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final EventRepository eventRepository;
    private final MentorshipService mentorshipService;

    @Transactional
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        if (!user.isActive()) {
            throw new UserDeactivatedException();
        }

        removeGoalsFromUser(user);
        removeEventsFromUser(user);
        mentorshipService.deleteMentorFromMentees(user.getId(), user.getMentees());

        user.setActive(false);
        userRepository.save(user);
    }

    private void removeGoalsFromUser(User user) {
        for (Goal goal : new ArrayList<>(user.getGoals())) {
            if (goal.getUsers().size() == 1) {
                goalRepository.delete(goal);
            }

            user.removeGoal(goal);
        }
    }

    private void removeEventsFromUser(User user) {
        if (user.getOwnedEvents() == null) {
            return;
        }

        List<Event> plannedEvents = user.getOwnedEvents()
                .stream()
                .filter(event -> event.getStatus().equals(EventStatus.PLANNED))
                .toList();

        for (Event event : plannedEvents) {
            event.setStatus(EventStatus.CANCELED);
            eventRepository.save(event);
            eventRepository.delete(event);
        }
    }
}
