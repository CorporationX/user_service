package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class UserService {
    private final MentorshipService mentorshipService;

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final GoalRepository goalRepository;

    @Transactional
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        deactivateUserDependencies(userId);

        user.setActive(false);
        userRepository.save(user);

        mentorshipService.stopUserMentorship(userId);
    }

    private void deactivateUserDependencies(Long userId) {
        removeUserFromGoals(userId);
        removeUserEvents(userId);
    }

    private void removeUserFromGoals(Long userId) {
        List<Goal> userGoals =  goalRepository.findGoalsByUserId(userId).toList();

        List<Goal> goalsToDelete = userGoals.stream()
                .filter(goal -> goal.getUsers().size() == 1)
                .toList();


        List<Goal> goalsToUpdate = userGoals.stream()
                .filter(goal -> goal.getUsers().size() > 1)
                .peek(goal -> goal.getUsers().removeIf(user -> Objects.equals(user.getId(), userId)))
                .toList();

        goalRepository.deleteAll(goalsToDelete);
        goalRepository.saveAll(goalsToUpdate);
    }

    private void removeUserEvents(Long userId) {
        List<Event> eventsOwnedToCancel = eventRepository.findAllByUserId(userId).stream()
                .filter(event -> Objects.equals(event.getOwner().getId(), userId))
                .peek(event -> event.setStatus(EventStatus.CANCELED))
                .toList();

        List<Event> eventsParticipatedToUpdate = eventRepository.findParticipatedEventsByUserId(userId).stream()
                .filter(event -> !Objects.equals(event.getOwner().getId(), userId))
                .peek(event -> event.getAttendees().removeIf(attendee ->
                        Objects.equals(attendee.getId(), userId)))
                .toList();

        List<Event> allEvents = new ArrayList<>();
        allEvents.addAll(eventsOwnedToCancel);
        allEvents.addAll(eventsParticipatedToUpdate);

        eventRepository.saveAll(allEvents);
    }
}