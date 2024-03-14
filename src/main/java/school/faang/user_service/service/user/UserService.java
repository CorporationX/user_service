package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MentorshipService mentorshipService;
    private final EventService eventService;
    private final GoalService goalService;

    public void deactivateUser(long userToDeactivateId) {
        User userToDeactivate = userRepository.findById(userToDeactivateId).get(); // в контроллере проверил, что такой User существует

        if (userToDeactivate.getMentees() != null && !userToDeactivate.getMentees().isEmpty()) {
            List<Long> userMenteesIds = userToDeactivate.getMentees().stream()
                    .map(User::getId)
                    .toList();
            userToDeactivate.setMentees(Collections.emptyList());
            mentorshipService.deleteMentorForAllHisMentees(userToDeactivateId, userMenteesIds);
        }

        if (userToDeactivate.getGoals() != null && !userToDeactivate.getGoals().isEmpty()) {
            List<Long> goalsToDeleteIds = userToDeactivate.getGoals().stream()
                    .filter(goal -> !GoalStatus.COMPLETED.equals(goal.getStatus()))
                    .peek(goal -> goal.getUsers().removeIf(user -> user.getId() == userToDeactivateId))
                    .filter(goal -> goal.getUsers().isEmpty())
                    .map(Goal::getId)
                    .toList();
            userToDeactivate.setGoals(Collections.emptyList());
            goalsToDeleteIds.forEach(goalService::deleteGoal);
        }

        if (userToDeactivate.getOwnedEvents() != null && !userToDeactivate.getOwnedEvents().isEmpty()) {
            List<Long> eventsToDeleteIds = userToDeactivate.getOwnedEvents().stream()
                    .filter(event -> EventStatus.PLANNED.equals(event.getStatus()))
                    .map(Event::getId)
                    .toList();
            userToDeactivate.setOwnedEvents(Collections.emptyList());
            eventsToDeleteIds.forEach(eventService::deleteEvent);
        }
        userToDeactivate.setActive(false);
        userRepository.save(userToDeactivate);
    }
}
