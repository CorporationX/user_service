package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.user.mentorship.MentorshipService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final GoalService goalService;
    private final EventService eventService;
    private final MentorshipService mentorshipService;

    @Override
    @Transactional
    public User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %s not found", id)));
    }

    @Override
    @Transactional
    public void deactivateUserById(Long id) {
        User user = findUserById(id);
        List<Goal> userGoals = user.getGoals();

        userGoals.forEach(goal -> {
            List<User> goalUsers = goal.getUsers();
            goalUsers.remove(user);
            if (goalUsers.isEmpty()) {
                goalService.delete(goal);
            }
        });

        eventService.deleteAll(user.getOwnedEvents());
        mentorshipService.deleteMentorFromMentee(user);

        user.setActive(false);
        userRepository.save(user);
    }
}
