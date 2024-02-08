package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    @Setter
    private EventService eventService;
    @Setter
    private MentorshipService mentorshipService;
    private final UserRepository userRepository;
    private final GoalService goalService;
    private final static long MOTHS_TO_DELETE_USER = 3;

    public User getExistingUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id = " + id + " is not found in database"));
    }

    public void checkIfOwnerExists(Long id) {
        if (!userRepository.existsById(id)) {
            throw new DataValidationException("Owner does not exist");
        }
    }

    public User findUserById(long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new DataValidationException(String.format("User with ID %d not found", id)));
    }

    public boolean checkIfOwnerExistsById(Long id) {
        return userRepository.existsById(id);
    }

    public User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User with id " + id + " is not exists"));
    }

    public void saveUser(User savedUser) {
        userRepository.save(savedUser);
    }

    public boolean existsUserById(long id) {
        return userRepository.existsById(id);
    }

    public void deactivationUserById(long userId) {
        User user = getUserById(userId);
        stopGoalsAndDeleteEventsAndDeleteMentor(user);
        user.setActive(false);
        try {
            saveUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "${my.schedule.cron}")
    public void deleteNonActiveUsers() {
        LocalDateTime timeToDelete = LocalDateTime.now().minusMonths(MOTHS_TO_DELETE_USER);
        userRepository.deleteAllInactiveUsersAndUpdatedAtOverMonths(timeToDelete);
    }

    private void stopGoalsAndDeleteEventsAndDeleteMentor(User user) {
        List<Goal> goals = user.getGoals();
        for (Goal goal : goals) {
            if (goal.getUsers().size() == 1) {
                goalService.deleteGoalById(goal.getId());
            }
        }

        List<Event> events = user.getOwnedEvents();
        for (Event event : events) {
            eventService.deleteEventById(event.getId());
        }

        List<User> mentees = user.getMentees();
        for (User mentee : mentees) {
            mentorshipService.deleteMentor(mentee.getId(), user.getId());
        }
    }

}
