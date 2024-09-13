package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final GoalService goalService;
    private final EventService eventService;
    private final MentorshipService mentorshipService;

    @Override
    public void deactivateUserProfile(long id) {
        User userToDeactivate = userRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Incorrect user id"));

        removeGoals(userToDeactivate);
        removeEvents(userToDeactivate);
        userToDeactivate.setActive(false);
        stopMentorship(userToDeactivate);

        userRepository.save(userToDeactivate);
    }

    private void stopMentorship(User userToDeactivate) {
        mentorshipService.deleteMentorFromMentees(userToDeactivate.getId(), userToDeactivate.getMentees());
    }

    private void removeEvents(User userToDeactivate) {
        List<Event> eventsToRemove = userToDeactivate.getOwnedEvents()
                .stream()
                .filter(event -> event.getStatus() == EventStatus.PLANNED)
                .map(event -> {
                    event.setStatus(EventStatus.CANCELED);
                    return event;
                })
                .toList();

        eventService.removeEvents(eventsToRemove);
    }

    private void removeGoals(User userToDeactivate) {
        List<Goal> goalsToRemove = userToDeactivate.getGoals()
                .stream()
                .map(goal -> {
                    goal.getUsers().removeIf(user -> user.getId().equals(userToDeactivate.getId()));
                    return goal;
                })
                .filter(goal -> goal.getUsers().isEmpty())
                .toList();

        goalService.removeGoals(goalsToRemove);
    }
}
