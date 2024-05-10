package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final EventRepository eventRepository;
    private final MentorshipRepository mentorshipRepository;

    @Transactional
    public void deactivate(long id) {
        log.info("Deactivate user with ID: {}", id);

        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("User doesn't exist by id: %s", id)));

        deleteMentorFromMentees(user);
        deleteUserGoals(user);
        deleteUserEvents(user);

        user.setActive(false);
        userRepository.save(user);
    }

    @Transactional
    private void deleteMentorFromMentees(User user) {
        log.info("Update mentor in user's setGoals where he was mentor");
        mentorshipRepository.deleteAllMentorshipByMentorId(user.getId());
        for (Goal goal : user.getSetGoals()) {
            log.info("Remove mentor from goal with ID: {}", goal.getId());
            goal.setMentor(null);
            goalRepository.save(goal);
        }
    }

    @Transactional
    private void deleteUserEvents(User user) {
        log.info("Delete user's events");
        List<Long> eventsForRemove = new ArrayList<>();
        for (Event event : user.getParticipatedEvents()) {
            if (event.getOwner().getId() == user.getId()) {
                log.info("Event with ID: {} will be deleted", event.getId());
                eventsForRemove.add(event.getId());
            }
        }
        user.setParticipatedEvents(Collections.emptyList());
        eventsForRemove.forEach(eventRepository::deleteById);
    }


    @Transactional
    private void deleteUserGoals(User user) {
        log.info("Delete user's goals");
        List<Long> goalsForRemove = new ArrayList<>();

        for (Goal goal : user.getGoals()) {
            if (goal.getUsers().size() == 1 && goal.getUsers().get(0).equals(user)) {
                log.info("Goal with ID: {} will be deleted", goal.getId());
                goalsForRemove.add(goal.getId());
            }
        }
        user.setGoals(Collections.emptyList());
        goalsForRemove.forEach(goalRepository::deleteById);
    }
}
