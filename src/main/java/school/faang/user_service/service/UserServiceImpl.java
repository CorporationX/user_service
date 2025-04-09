package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final int SINGLE_USER = 1;

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final EventRepository eventRepository;
    private final MentorshipService mentorshipService;

    @Override
    public void deactivateUser(Long userId) {
        User user = findUserById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));

        stopUserGoals(user);
        stopUserEvents(user);
        mentorshipService.stopMentoringIfMentor(user);

        user.setActive(false);
        userRepository.save(user);
    }

    private void stopUserGoals(User user) {
        List<Goal> userGoals = goalRepository.findGoalsByUserId(user.getId()).toList();

        for (Goal goal : userGoals) {
            List<User> participants = goalRepository.findUsersByGoalId(goal.getId());

            boolean isOnlyParticipant = participants.size() == SINGLE_USER
                    && participants.get(0).getId().equals(user.getId());

            if (isOnlyParticipant) {
                goalRepository.delete(goal);
            } else {
                participants.removeIf(u -> u.getId().equals(user.getId()));
                goal.setUsers(participants);

                if (goal.getMentor() != null && goal.getMentor().getId().equals(user.getId())) {
                    goal.setMentor(null);
                }

                goalRepository.save(goal);
            }
        }
    }

    private void stopUserEvents(User user) {
        List<Event> events = eventRepository.findAllByUserId(user.getId());

        for (Event event : events) {
            event.setStatus(EventStatus.CANCELED);
            eventRepository.save(event);
        }
    }

    @Override
    public Optional<User> findUserById(long userId) {
        return userRepository.findById(userId);
    }
}
