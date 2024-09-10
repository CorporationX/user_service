package school.faang.user_service.service.User;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.Mentorship.MentorshipServiceImpl;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final EventRepository eventRepository;
    private final MentorshipServiceImpl mentorshipService;
    private final UserMapper userMapper;

    public UserDto deactivateUser(Long id) {
        User currentUser = userRepository.findById(id).orElseThrow();

        List<Event> userEvents = currentUser.getOwnedEvents();
        List<Goal> currentUserGoals = currentUser.getGoals();
        List<User> mentees = currentUser.getMentees();

        for (Goal userGoal : currentUserGoals) {
            List<User> usersWithGoal = goalRepository.findUsersByGoalId(userGoal.getId());
            if (usersWithGoal.size() < 2) {
                goalRepository.delete(userGoal);
            }
        }

        for (User mentee : mentees) {
            mentorshipService.stopMentorship(currentUser.getId(), mentee.getId());
        }

        for (Event event : userEvents) {
            for (User user : event.getAttendees()) {
                user.getParticipatedEvents().remove(event);
            }
            eventRepository.delete(event);
        }
        currentUser.setGoals(new ArrayList<>());
        currentUser.setOwnedEvents(new ArrayList<>());

        return userMapper.toDto(currentUser);
    }
}
