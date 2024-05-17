package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;

    @Transactional
    public void deactivate(long id) {
        log.info("Deactivate user with ID: {}", id);

        User user = findById(id);

        user.getMentees().forEach(mentee -> mentee.getMentors().remove(user));
        deleteMentorFromUsersGoals(user);
        deleteUserGoals(user);
        deleteUserEvents(user);

        user.setActive(false);
        userRepository.save(user);
    }

    public User findById(long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("User doesn't exist by id: %s", id)));
    }

    @Transactional
    private void deleteMentorFromUsersGoals(User user) {
        log.info("Update mentor in user's setGoals where he was mentor");
        for (Goal goal : user.getSetGoals()) {
            log.info("Remove mentor from goal with ID: {}", goal.getId());
            goal.setMentor(null);
            goalRepository.save(goal);
        }
    }

    @Transactional
    private void deleteUserEvents(User user) {
        log.info("Delete user's events");
        user.getOwnedEvents().removeIf(event -> event.getOwner().getId() == user.getId());
        user.setParticipatedEvents(Collections.emptyList());
    }

    @Transactional
    private void deleteUserGoals(User user) {
        log.info("Delete user's goals");
        user.getGoals().forEach(goal -> {
            if (goal.getUsers().size() == 1 && goal.getUsers().get(0).equals(user)) {
                goalRepository.deleteById(goal.getId());
            }
            goal.getUsers().remove(user);
        });
    }
}
