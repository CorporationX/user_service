package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
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
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GoalService goalService;
    private final EventRepository eventRepository;
    private final MentorshipService mentorshipService;

    @Transactional
    public void deactivateUser(Long userId) {
        User user = findById(userId);

        if (!user.isActive()) {
            throw new UserDeactivatedException();
        }

        removeUserGoals(user);
        removeUserEvents(user);
        mentorshipService.deleteMentorFromMentees(user.getId(), user.getMentees());

        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    private void removeUserGoals(User user) {
        user.getGoals().forEach(goal -> {
            if (goal.getUsers().size() == 1) {
                goalService.deleteGoalAndUnlinkChildren(goal);
            }

            goal.getUsers().remove(user);
        });

        user.getGoals().clear();
    }

    private void removeUserEvents(User user) {
        if (user.getOwnedEvents() == null) {
            return;
        }

        List<Event> plannedEvents = user.getOwnedEvents()
                .stream()
                .filter(event -> event.getStatus().equals(EventStatus.PLANNED))
                .toList();

        plannedEvents.forEach(event -> {
            event.setStatus(EventStatus.CANCELED);
            eventRepository.save(event);
            eventRepository.delete(event);
        });
    }
}
