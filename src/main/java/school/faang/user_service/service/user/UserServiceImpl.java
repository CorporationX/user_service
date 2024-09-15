package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final EventRepository eventRepository;
    private final MentorshipService mentorshipService;

    @Transactional
    public void deactivateUser(Long id) {
        User userToDeactivate = userRepository.findById(id).orElseThrow();

        List<Event> userToDeactivateOwnedEvents = userToDeactivate.getOwnedEvents();
        List<Goal> userToDeactivateGoals = userToDeactivate.getGoals();
        List<User> userToDeactivateMentees = userToDeactivate.getMentees();

        for (Goal userGoal : userToDeactivateGoals) {
            List<User> usersWithGoal = goalRepository.findUsersByGoalId(userGoal.getId());
            if (usersWithGoal.size() < 2) {
                goalRepository.delete(userGoal);
            }
        }

        for (User mentee : userToDeactivateMentees) {
            mentorshipService.stopMentorship(userToDeactivate.getId(), mentee.getId());
        }

        eventRepository.deleteAll(userToDeactivateOwnedEvents);

        userToDeactivate.setGoals(new ArrayList<>());
        //как я понял по заданию, что если цель есть не только у аккаунта который деактивируется, то тогда она остаётся
        //в БД (но не у деактивируемого). Если она есть только у аккаунта который деактивируется, то тогда нужно удалить цель из бд.
        //Пустой список ставится только у деактивиранного аккаунта в любом случае, потому что для этого аккаунта
        //целей не будет. (тк он деактивирован)
        userToDeactivate.setOwnedEvents(new ArrayList<>());
    }
}
