package school.faang.user_service.service.mentorship;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.adapter.GoalRepositoryAdapter;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final GoalRepositoryAdapter goalRepositoryAdapter;

    @Transactional
    public void stopMentorship(User mentor) {
        long mentorId = mentor.getId();

        List<User> mentees = mentor.getMentees();
        for (User mentee : mentees) {
            mentee.getMentors().remove(mentor);
            log.info(
                    "User with ID {} is no longer the mentor of the user with ID {}",
                    mentorId,
                    mentee.getId());
        }
        goalRepositoryAdapter
                .findGoalsByMentorId(mentor.getId())
                .forEach(
                        goal -> {
                            goal.setMentor(null);
                            log.info(
                                    "Mentor has been removed from the goal with ID {}",
                                    goal.getId());
                        });
    }
}
