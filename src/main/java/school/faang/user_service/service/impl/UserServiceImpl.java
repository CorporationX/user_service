package school.faang.user_service.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.UserService;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private EventRepository eventRepository;
    private GoalRepository goalRepository;

    @Override
    @Transactional
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with [%s] not found", userId)));

        deactivateUserActivities(user);
        user.setActive(false);
        deactivateMentorship(user);
    }

    private void deactivateUserActivities(User user) {
        List<Event> ownedEvents = eventRepository.findAllByUserId(user.getId());
        ownedEvents.forEach(event -> eventRepository.deleteById(event.getId()));

        List<Event> participatedEvents = eventRepository.findParticipatedEventsByUserId(user.getId());
        participatedEvents.forEach(event -> event.getAttendees().remove(user));

        List<Goal> userGoals = goalRepository.findGoalsByUserId(user.getId()).toList();
        userGoals.forEach(goal -> {
            List<User> goalUsers = goalRepository.findUsersByGoalId(goal.getId());
            if (goalUsers.size() == 1) {
                goalRepository.deleteById(goal.getId());
            } else {
                goal.getUsers().remove(user);
            }
        });
    }

    private void deactivateMentorship(User user) {
        user.getMentees().forEach(mentee -> mentee.getMentors().remove(user));
        user.getMentors().forEach(mentor -> mentor.getMentees().remove(user));

        List<Goal> mentoredGoals = goalRepository.findAllByMentorId(user.getId());
        mentoredGoals.forEach(goal -> goal.setMentor(null));
    }
}
