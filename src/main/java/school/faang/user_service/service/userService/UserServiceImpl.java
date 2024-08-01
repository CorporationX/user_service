package school.faang.user_service.service.userService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.mentorshipService.MentorshipServiceImpl;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;

    @Override
    public void deactivateUser(long userId){

        User user = userRepository.findById(userId).orElseThrow();
        user.setActive(false);
        userRepository.save(user);

//        this whole thing doesnt account for parent and child goals
        List<Goal> userGoals = goalRepository.findGoalsByUserId(userId).toList();
        List<Long> goalIdsToBeDeleted = new ArrayList<>();

//        Get list of all user and then remove one we want to deactivate. Then if resulted list of users is empty
//        goal is done by that person and remove, otherwise just update list
        userGoals.forEach(goal -> {
            List<User> goalUsers = goal.getUsers();

//            avoid exception if null, should be impossible but still
            if (goalUsers == null) {
                return;
            }

            goalUsers.removeIf(goalUser -> goalUser.getId() == userId);
            if(goal.getUsers().isEmpty()){
                goalRepository.deleteById(goal.getId());
            } else {
                goal.setUsers(goalUsers);
            }
        });


    }
}
