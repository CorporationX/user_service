package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final static long MOTHS_TO_DELETE_USER = 3;

    private final UserRepository userRepository;
    @Setter
    private EventService eventService;
    @Setter
    private MentorshipService mentorshipService;
    @Setter
    private GoalService goalService;

    public void deactivationUserById(long userId) {
        User user = getUserById(userId);
        stopGoalsAndDeleteEventsAndDeleteMentor(user);

        user.setActive(false);
        saveUser(user);
    }

    @Scheduled(cron = "${my.schedule.cron}")
    public void deleteNonActiveUsers() {
        LocalDateTime timeToDelete = LocalDateTime.now().minusMonths(MOTHS_TO_DELETE_USER);
        //userRepository.deleteAllInactiveUsersAndUpdatedAtOverMonths(timeToDelete);
    }

    public boolean isOwnerExistById(Long id) {
        return userRepository.existsById(id);
    }

    public void saveUser(User savedUser) {
        if (isOwnerExistById(savedUser.getId())) {
            userRepository.save(savedUser);
        }
    }

    public User getUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with ID %d not found", id)));
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
