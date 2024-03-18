package school.faang.user_service.controller.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeactivationService {
    private final UserRepository userRepository;
    private final GoalService goalService;
    private final EventService eventService;
    private final MentorshipService mentorshipService;
    private final UserMapper userMapper;

    public UserDto deactivateUser(long userId) {
        User userToDeactivate = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User doesn't exist by id: " + userId));

        if (userToDeactivate.getGoals() != null && !userToDeactivate.getGoals().isEmpty()) {
            List<Long> goalsToDeleteIds = userToDeactivate.getGoals().stream()
                    .filter(goal -> !GoalStatus.COMPLETED.equals(goal.getStatus()))
                    .peek(goal -> goal.getUsers().removeIf(user -> user.getId() == userId))
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

        if (userToDeactivate.getMentees() != null && !userToDeactivate.getMentees().isEmpty()) {
            mentorshipService.deleteMentorForAllHisMentees(userId, userToDeactivate.getMentees());
            userToDeactivate.setMentees(Collections.emptyList());
        }
        return userMapper.toDto(userRepository.save(userToDeactivate));
    }
}
