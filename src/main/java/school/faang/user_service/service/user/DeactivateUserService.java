package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeactivateUserService {
    private final GoalRepository goalRepository;
    private final EventRepository eventRepository;

    public User stopUserActivities(User user) {
        stopGoals(user);
        stopEvents(user);
        return user;
    }

    public User stopGoals(User user) {
        List<Goal> goals = user.getGoals();
        for (Goal goal : goals) {
            if (goal.getUsers().size() == 1) {
                goalRepository.deleteById(goal.getId());
            }
        }
        user.setGoals(new ArrayList<>());
        return user;
    }

    public User stopEvents(User user) {
        List<Event> events = user.getOwnedEvents();
        for (Event event : events) {
            eventRepository.deleteById(event.getId());
        }
        user.setOwnedEvents(new ArrayList<>());
        return user;
    }
}
