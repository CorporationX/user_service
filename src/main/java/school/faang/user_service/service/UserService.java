package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class UserService {
    private final MentorshipService mentorshipService;

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final GoalRepository goalRepository;

    public void deactivateUser(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
        onBeforeDeactivateUser(userId);
        userRepository.findById(userId).get()
                .setActive(false);
        onAfterDeactivateUser(userId);
    }

    private void onBeforeDeactivateUser(Long userId) {
        removeUserFromGoals(userId);
        removeUserEvents(userId);
    }

    private void onAfterDeactivateUser(Long userId) {
        mentorshipService.stopUserMentorship(userId);
    }

    private void removeUserFromGoals(Long userId) {
        goalRepository.findGoalsByUserId(userId).forEach(goal -> {
            if (goal.getUsers() == null || goal.getUsers().isEmpty()) {
                throw new IllegalStateException("Goal has no associated users!");
            } else if (goal.getUsers().size() == 1) {
                goalRepository.delete(goal);
            } else {
                goal.getUsers().removeIf(user -> Objects.equals(user.getId(), userId));
                goalRepository.save(goal);
            }
        });
    }

    private void removeUserEvents(Long userId) {
        eventRepository.findAllByUserId(userId).forEach(event -> {
            event.setStatus(EventStatus.CANCELED);
        });

        eventRepository.findParticipatedEventsByUserId(userId).forEach(event -> {
            event.setAttendees(event.getAttendees().stream()
                    .filter(attendee -> !Objects.equals(attendee.getId(), userId))
                    .toList());
            eventRepository.save(event);
        });
    }
}